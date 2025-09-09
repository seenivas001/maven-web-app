package tn.esprit.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;
import tn.esprit.spring.services.SubscriptionServicesImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class SubscriptionServicesImplTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository;

    @InjectMocks
    private SubscriptionServicesImpl subscriptionServices;

    private Subscription monthlySubscription;
    private Subscription annualSubscription;

    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();

        monthlySubscription = new Subscription();
        monthlySubscription.setStartDate(now);
        monthlySubscription.setTypeSub(TypeSubscription.MONTHLY);
        monthlySubscription.setPrice(100.0f);

        annualSubscription = new Subscription();
        annualSubscription.setStartDate(now);
        annualSubscription.setTypeSub(TypeSubscription.ANNUAL);
        annualSubscription.setPrice(900.0f);
    }

    // ===== TESTS POSITIFS =====

    @Test
    void addSubscription_withMonthlyType_shouldSetEndDateAndSave() {
        // Arrange
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(monthlySubscription);

        // Act
        Subscription result = subscriptionServices.addSubscription(monthlySubscription);

        // Assert
        assertEquals(monthlySubscription.getStartDate().plusMonths(1), result.getEndDate());
        verify(subscriptionRepository).save(monthlySubscription);
    }

    @Test
    void addSubscription_withAnnualType_shouldSetEndDateCorrectly() {
        // Arrange
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(annualSubscription);

        // Act
        Subscription result = subscriptionServices.addSubscription(annualSubscription);

        // Assert
        assertEquals(annualSubscription.getStartDate().plusYears(1), result.getEndDate());
    }

    @Test
    void updateSubscription_shouldSaveAndReturnUpdatedSubscription() {
        // Arrange
        annualSubscription.setPrice(1000.0f);
        when(subscriptionRepository.save(any())).thenReturn(annualSubscription);

        // Act
        Subscription result = subscriptionServices.updateSubscription(annualSubscription);

        // Assert
        assertEquals(1000.0f, result.getPrice());
        verify(subscriptionRepository).save(annualSubscription);
    }

    @Test
    void retrieveSubscriptionById_whenExists_shouldReturnSubscription() {
        // Arrange
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(annualSubscription));

        // Act
        Subscription result = subscriptionServices.retrieveSubscriptionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(annualSubscription, result);
    }

    @Test
    void getSubscriptionByType_shouldReturnSetOfSubscriptions() {
        Set<Subscription> expectedSet = new HashSet<>(List.of(annualSubscription));
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL)).thenReturn(expectedSet);

        Set<Subscription> result = subscriptionServices.getSubscriptionByType(TypeSubscription.ANNUAL);

        assertEquals(1, result.size());
        assertTrue(result.contains(annualSubscription));
    }

    @Test
    void retrieveSubscriptionsByDates_shouldReturnListInRange() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);

        when(subscriptionRepository.getSubscriptionsByStartDateBetween(start, end)).thenReturn(List.of(monthlySubscription));

        List<Subscription> result = subscriptionServices.retrieveSubscriptionsByDates(start, end);

        assertEquals(1, result.size());
    }

    // ===== TESTS NÉGATIFS =====

    @Test
    void retrieveSubscriptionById_whenNotFound_shouldReturnNull() {
        when(subscriptionRepository.findById(999L)).thenReturn(Optional.empty());

        Subscription result = subscriptionServices.retrieveSubscriptionById(999L);

        assertNull(result);
    }

    // ===== TEST COMPORTEMENTAL (retrieveSubscriptions scheduler) =====

    @Test
    void retrieveSubscriptions_scheduler_shouldLogInfoForEachSubscription() {
        // Arrange
        Subscription expired = new Subscription(1L, LocalDate.now().minusMonths(2), LocalDate.now().minusDays(1), 200.0f, TypeSubscription.MONTHLY);
        Skier skier = new Skier();
        skier.setFirstName("Ali");
        skier.setLastName("Ben Salah");

        when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(List.of(expired));
        when(skierRepository.findBySubscription(expired)).thenReturn(skier);

        // Act
        subscriptionServices.retrieveSubscriptions();

        // Assert
        verify(subscriptionRepository).findDistinctOrderByEndDateAsc();
        verify(skierRepository).findBySubscription(expired);
    }
}
