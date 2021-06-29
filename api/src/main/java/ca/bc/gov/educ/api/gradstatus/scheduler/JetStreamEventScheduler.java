package ca.bc.gov.educ.api.gradstatus.scheduler;

import ca.bc.gov.educ.api.gradstatus.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.gradstatus.repository.GradStatusEventRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static ca.bc.gov.educ.api.gradstatus.constant.EventStatus.DB_COMMITTED;

/**
 * This class is responsible to check the GRAD_STATUS_EVENT table periodically and publish messages to JET STREAM, if some them are not yet published
 * this is a very edge case scenario which will occur.
 */
@Component
@Slf4j
public class JetStreamEventScheduler {

  private final GradStatusEventRepository gradStatusEventRepository;
  private final Publisher publisher;

  /**
   * Instantiates a new Stan event scheduler.
   *
   * @param gradStatusEventRepository the grad status event repository
   * @param publisher                 the publisher
   */
  public JetStreamEventScheduler(GradStatusEventRepository gradStatusEventRepository, Publisher publisher) {
    this.gradStatusEventRepository = gradStatusEventRepository;
    this.publisher = publisher;
  }

  /**
   * Find and publish grad status events to stan.
   */
  @Scheduled(cron = "0 0/5 * * * *") // every 5 minutes
  @SchedulerLock(name = "PUBLISH_GRAD_STATUS_EVENTS_TO_JET_STREAM", lockAtLeastFor = "PT4M", lockAtMostFor = "PT4M")
  public void findAndPublishGradStatusEventsToJetStream() {
    LockAssert.assertLocked();
    var results = gradStatusEventRepository.findByEventStatus(DB_COMMITTED.toString());
    if (!results.isEmpty()) {
      results.forEach(el -> {
        if (el.getUpdateDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
          try {
            publisher.dispatchChoreographyEvent(el);
          } catch (final Exception ex) {
            log.error("Exception while trying to publish message", ex);
          }
        }
      });
    }

  }
}
