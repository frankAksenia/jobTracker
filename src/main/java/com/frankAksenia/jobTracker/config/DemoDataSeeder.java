package com.frankAksenia.jobTracker.config;

import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frankAksenia.jobTracker.model.EApplicationStatus;
import com.frankAksenia.jobTracker.model.EInterviewType;
import com.frankAksenia.jobTracker.model.Interview;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.Note;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.model.EUserRole;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;
import com.frankAksenia.jobTracker.repository.InterviewRepository;
import com.frankAksenia.jobTracker.repository.NoteRepository;
import com.frankAksenia.jobTracker.repository.UserRepository;

@Component
@ConditionalOnProperty(name = "jobtracker.demo.seed.enabled", havingValue = "true", matchIfMissing = false)
public class DemoDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final NoteRepository noteRepository;
    private final PasswordEncoder passwordEncoder;
    private final String seedPassword;

    public DemoDataSeeder(UserRepository userRepository, ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository, NoteRepository noteRepository,
            PasswordEncoder passwordEncoder,
            @Value("${jobtracker.demo.seed.password:}") String seedPassword) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.noteRepository = noteRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedPassword = seedPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int seedPasswordBytes = seedPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        if (seedPasswordBytes < 12 || seedPasswordBytes > 72) {
            throw new IllegalStateException("When demo seeding is enabled, DEMO_SEED_PASSWORD must be 12 to 72 UTF-8 bytes.");
        }
        LocalDate today = LocalDate.now();
        seedMaya(today);
        seedAlex(today);
    }

    private void seedMaya(LocalDate today) {
        User maya = resetSeedUser("Maya", "Chen", "maya.chen@example.test");
        createApplication(maya, "Cloudberry Labs", "Junior Java Developer", "Vienna, Austria",
                EApplicationStatus.SAVED, null,
                "Saved for later. Tailor the CV to highlight the Spring Boot portfolio project.");
        createApplication(maya, "Willow Analytics", "Backend Developer", "Remote",
                EApplicationStatus.APPLIED, today.minusDays(5),
                "Submitted a CV and cover letter through the fictional company careers page.");
        JobApplication interview = createApplication(maya, "Lantern Software", "Java Developer", "Graz, Austria",
                EApplicationStatus.INTERVIEW, today.minusDays(12),
                "Recruiter screening went well. Review REST APIs and JPA before the technical interview.");
        createInterview(interview, EInterviewType.HR, today.minusDays(3), 10, 30, "Robin Ellis",
                "Video call", "Discuss motivation, availability and the role.");
        createInterview(interview, EInterviewType.TECHNICAL, today.plusDays(3), 14, 60, "Sam Rivera",
                "Video call", "Pair programming with Java and questions about database relationships.");

        JobApplication offer = createApplication(maya, "Meadow Systems", "Software Engineer", "Linz, Austria",
                EApplicationStatus.OFFER, today.minusDays(24),
                "Received a fictional offer. Compare mentoring opportunities and the proposed start date.");
        createInterview(offer, EInterviewType.FINAL, today.minusDays(6), 11, 45, "Jamie Brooks",
                "Linz office", "Meet the team lead and discuss onboarding.");
        createApplication(maya, "Pebble Networks", "API Developer", "Salzburg, Austria",
                EApplicationStatus.REJECTED, today.minusDays(18),
                "The team selected a candidate with more experience. Keep the feedback for future applications.");
        createApplication(maya, "Maple Digital", "Full Stack Developer", "Remote",
                EApplicationStatus.WITHDRAWN, today.minusDays(15),
                "Withdrew because the role required travel that did not fit the current schedule.");
    }

    private void seedAlex(LocalDate today) {
        User alex = resetSeedUser("Alex", "Morgan", "alex.morgan@example.test");
        createApplication(alex, "Silver Fern Studio", "Frontend Developer", "Vienna, Austria",
                EApplicationStatus.SAVED, null,
                "Bookmark the role and prepare examples of accessible user interfaces.");
        createApplication(alex, "Harbor Metrics", "Data Analyst", "Remote",
                EApplicationStatus.APPLIED, today.minusDays(4),
                "Sent a portfolio with SQL analysis and dashboard examples. Follow up next week.");
        JobApplication interview = createApplication(alex, "Northstar Orchard", "Product Analyst", "Graz, Austria",
                EApplicationStatus.INTERVIEW, today.minusDays(10),
                "Prepare examples of teamwork and practice explaining a product experiment.");
        createInterview(interview, EInterviewType.BEHAVIORAL, today.minusDays(2), 9, 45, "Taylor Quinn",
                "Video call", "Discuss collaboration, prioritization and learning from feedback.");
        createInterview(interview, EInterviewType.CASE_STUDY, today.plusDays(5), 13, 60, "Casey Lane",
                "Graz office", "Present an analysis of a fictional product conversion funnel.");

        JobApplication offer = createApplication(alex, "Juniper Works", "UX Researcher", "Remote",
                EApplicationStatus.OFFER, today.minusDays(21),
                "Received a fictional offer after the portfolio discussion. Review the learning budget.");
        createInterview(offer, EInterviewType.OTHER, today.minusDays(7), 15, 45, "Drew Parker",
                "Video call", "Portfolio walkthrough and an informal discussion with the research team.");
        createApplication(alex, "Bluebird Tools", "Business Analyst", "Linz, Austria",
                EApplicationStatus.REJECTED, today.minusDays(16),
                "Role was filled internally. Ask to be considered for a future opening.");
        createApplication(alex, "Cedar Compass", "Project Coordinator", "Salzburg, Austria",
                EApplicationStatus.WITHDRAWN, today.minusDays(13),
                "Withdrew to focus on roles with more research and analysis responsibilities.");
    }

    private User resetSeedUser(String firstName, String lastName, String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            for (JobApplication application : applicationRepository.findAllByUser(user)) {
                interviewRepository.deleteAll(interviewRepository.findAllByJobApplication(application));
                noteRepository.deleteAll(noteRepository.findAllByJobApplication(application));
                applicationRepository.delete(application);
            }
            userRepository.delete(user);
            userRepository.flush();
        });
        return createUser(firstName, lastName, email);
    }

    private User createUser(String firstName, String lastName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(seedPassword));
        user.setUserRole(EUserRole.APPLICANT);
        return userRepository.save(user);
    }

    private JobApplication createApplication(User user, String company, String position, String location,
            EApplicationStatus status, LocalDate applicationDate, String noteContent) {
        JobApplication application = new JobApplication();
        application.setCompanyName(company);
        application.setPosition(position);
        application.setLocation(location);
        application.setJobUrl("https://jobs.example.test/openings");
        application.setSource("Fictional careers page");
        application.setStatus(status);
        application.setApplicationDate(applicationDate);
        application.setUser(user);
        JobApplication savedApplication = applicationRepository.save(application);

        Note note = new Note();
        note.setContent(noteContent);
        note.setJobApplication(savedApplication);
        noteRepository.save(note);
        return savedApplication;
    }

    private void createInterview(JobApplication application, EInterviewType type, LocalDate date, int hour,
            int durationMinutes, String interviewer, String location, String description) {
        Interview interview = new Interview();
        interview.setInterviewType(type);
        interview.setScheduledAt(date.atTime(hour, 0));
        interview.setDurationMinutes(durationMinutes);
        interview.setInterviewer(interviewer);
        interview.setLocation(location);
        interview.setDescription(description);
        interview.setJobApplication(application);
        interviewRepository.save(interview);
    }
}
