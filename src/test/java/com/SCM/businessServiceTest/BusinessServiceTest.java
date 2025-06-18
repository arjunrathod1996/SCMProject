package com.SCM.businessServiceTest;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.SCM.databaseConnectionPool.DatabaseConnectionPool;
import com.SCM.entities.Business;
import com.SCM.repository.BusinessRepository;
import com.SCM.service.BusinessService;
import com.SCM.testLogAppender.TestLogAppender;
import com.SCM.enums.Category;
import com.SCM.utils.Utils;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Single instance of the test class for the entire test run
public class BusinessServiceTest {

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private BusinessService businessService;

    @Captor
    private ArgumentCaptor<Business> businessCaptor;  // To capture arguments passed to mocks

    private Business newBusiness;
    private Business existingBusiness;
    private TestLogAppender testLogAppender;

    // Runs once before all test methods.
    @BeforeAll
    public void beforeAllTests() {
        System.out.println("Running @BeforeAll - Set up once for all tests.");
        // Example: Initialize a global resource, like a database connection pool
        DatabaseConnectionPool.initialize();  // Initialize the connection pool for all tests
    }

    // Runs once after all test methods.
    @AfterAll
    public void afterAllTests() {
        System.out.println("Running @AfterAll - Clean up once after all tests.");
        // Example: Clean up global resources
        DatabaseConnectionPool.shutdown();  // Shut down the connection pool after all tests
    }

    // Runs before each test method.
    @BeforeEach
    public void setUp() {
        System.out.println("Running @BeforeEach - Set up before each test.");
        // Initialize test data
        newBusiness = new Business();
        newBusiness.setId(null);
        newBusiness.setName("New Business");
        newBusiness.setDescription("A new business entity for testing.");
        newBusiness.setCreationTime(Utils.now());  // Ensure this is set
        newBusiness.setUpdateTime(Utils.now());    // Ensure this is set

        existingBusiness = new Business();
        existingBusiness.setId(1L);
        existingBusiness.setName("Existing Business");
        existingBusiness.setDescription("An existing business entity for testing.");
        existingBusiness.setCreationTime(Utils.now());  // Ensure this is set
        existingBusiness.setUpdateTime(Utils.now());    // Ensure this is set
    }

    // Runs after each test method.
    @AfterEach
    public void tearDown() {
        System.out.println("Running @AfterEach - Clean up after each test.");
        // Example: Clear temporary test data or reset mock interactions
        Mockito.reset(businessRepository);  // Reset mock repository to clear interactions
    }

    @Test
    @DisplayName("Test saving a new business")  // Custom display name for the test
    public void testSaveNewBusiness() {
        when(businessRepository.existsById(any(Long.class))).thenReturn(false);
        when(businessRepository.save(any(Business.class))).thenReturn(newBusiness);

        Business result = businessService.saveBusiness(newBusiness);
        assertEquals("New Business", result.getName(), "Business name should be 'New Business' after save.");

        // Verify the save method was called with the expected argument
        verify(businessRepository).save(businessCaptor.capture());
        assertEquals("New Business", businessCaptor.getValue().getName(), "Captured business name should be 'New Business'.");
    }

   
    @Test
    @DisplayName("Test updating an existing business")
    public void testUpdateExistingBusiness() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));
        when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);

        existingBusiness.setName("Sara");
        Business result = businessService.updateExistingBusiness(existingBusiness);

        assertEquals("Sara", result.getName(), "Business name should be 'Updated Business' after update.");
    }
    
//  @Test
//  public void testUpdateExistingBusiness() {
//      // Arrange
//	  when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));
//      when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);
//
//      // Act
//      existingBusiness.setName("sara");
//      Business result = businessService.updateExistingBusiness(existingBusiness);
//
//      // Assert
//      try {
//          // Check if the name of the updated business is what you expect
//          assertEquals("Sara", result.getName());
//          System.out.println("Test passed: Business updated successfully.");
//      } catch (AssertionError e) {
//          // Print the error message if the assertion fails
//          System.err.println("Test failed: " + e.getMessage());
//          throw e;  // Ensure the test fails
//      } finally {
//          // Clear logs for the next test
//          testLogAppender.clear();
//      }
//  }

    @Test
    @DisplayName("Test getting a business by page")
    public void testGetBusiness() {
        Page<Business> page = new PageImpl<>(List.of(existingBusiness));
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "creationTime"));

        when(businessRepository.findBusinessPageWisee(pageable)).thenReturn(page);

        Page<Business> result = businessService.getBusiness(pageable);
        assertEquals(1, result.getTotalElements(), "Total elements should be 1.");
        assertEquals(existingBusiness, result.getContent().get(0), "Fetched business should match the existing business.");
    }

    @Test
    @DisplayName("Test finding a business by user ID")
    public void testFindByUserId() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));

        Optional<Business> result = businessService.findByUserId(1L);
        assertTrue(result.isPresent(), "Business should be found by user ID.");
        assertEquals(existingBusiness, result.get(), "The business returned should match the existing business.");
    }

    @Test
    @DisplayName("Test deleting a business")
    public void testDeleteBusiness() {
        when(businessRepository.findById(existingBusiness.getId())).thenReturn(Optional.of(existingBusiness));
     //   when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);

        businessService.deleteBusiness(1L);

        assertTrue(existingBusiness.isDeleted(), "The business should be marked as deleted.");
    }
}


//@ExtendWith(MockitoExtension.class)
//public class BusinessServiceTest {
//
//    @Mock
//    private BusinessRepository businessRepository;
//
//    @InjectMocks
//    private BusinessService businessService;
//
//    private Business newBusiness;
//    private Business existingBusiness;
//
// // Runs once before all test methods.
//    @BeforeAll
//    public static void beforeAllTests() {
//        System.out.println("Running @BeforeAll - Set up once for all tests.");
//        // Example: Initialize a global resource, like a database connection pool
//        DatabaseConnectionPool.initialize();  // Initialize the connection pool for all tests
//    }
//
//    // Runs once after all test methods.
//    @AfterAll
//    public static void afterAllTests() {
//        System.out.println("Running @AfterAll - Clean up once after all tests.");
//        // Example: Clean up global resources
//        DatabaseConnectionPool.shutdown();  // Shut down the connection pool after all tests
//    }
//
//    // Runs before each test method.
//    @BeforeEach
//    public void setUp() {
//        System.out.println("Running @BeforeEach - Set up before each test.");
//        // Example: Create a fresh instance of a class or reset state
//        newBusiness = new Business();
//        newBusiness.setId(null);
//        newBusiness.setName("New Business");
//       // newBusiness.setCategory(Category.SERVICE);  // Example setting for category
//        newBusiness.setDescription("A new business entity for testing.");
//        newBusiness.setCreationTime(Utils.now());  // Ensure this is set
//        newBusiness.setUpdateTime(Utils.now());    // Ensure this is set
//
//        existingBusiness = new Business();
//        existingBusiness.setId(1L);
//        existingBusiness.setName("Existing Business");
//       // existingBusiness.setCategory(Category.PRODUCT);  // Example setting for category
//        existingBusiness.setDescription("An existing business entity for testing.");
//        existingBusiness.setCreationTime(Utils.now());  // Ensure this is set
//        existingBusiness.setUpdateTime(Utils.now());    // Ensure this is set
//    }
//
//    // Runs after each test method.
////    @AfterEach
////    public void tearDown() {
////        System.out.println("Running @AfterEach - Clean up after each test.");
////        // Example: Clear temporary test data or reset mock interactions
////        businessRepository.reset(); // Hypothetical method to reset the state of the mock repository
////    }
//    @AfterEach
//    public void tearDown() {
//        System.out.println("Running @AfterEach - Clean up after each test.");
//        // Example: Clear temporary test data or reset mock interactions
//        // Reset mocks if you have them, for instance:
//        Mockito.reset(businessRepository);  // Reset mock repository to clear interactions
//    }
//
//    @Test
//    public void testSaveNewBusiness() {
//        when(businessRepository.existsById(any(Long.class))).thenReturn(false);
//        when(businessRepository.save(any(Business.class))).thenReturn(newBusiness);
//
//        Business result = businessService.saveBusiness(newBusiness);
//        assertEquals("New Business", result.getName(), "Business name should be 'New Business' after save.");
//    }
//
//    @Test
//    public void testUpdateExistingBusiness() {
//        when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));
//        when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);
//
//        existingBusiness.setName("Updated Business");
//        Business result = businessService.updateExistingBusiness(existingBusiness);
//
//        assertEquals("Updated Business", result.getName(), "Business name should be 'Updated Business' after update.");
//    }
//
//    @Test
//    public void testGetBusiness() {
//        Page<Business> page = new PageImpl<>(List.of(existingBusiness));
//        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "creationTime"));
//
//        when(businessRepository.findBusinessPageWisee(pageable)).thenReturn(page);
//
//        Page<Business> result = businessService.getBusiness(pageable);
//        assertEquals(1, result.getTotalElements(), "Total elements should be 1.");
//        assertEquals(existingBusiness, result.getContent().get(0), "Fetched business should match the existing business.");
//    }
//
//    @Test
//    public void testFindByUserId() {
//        when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));
//
//        Optional<Business> result = businessService.findByUserId(1L);
//        assertTrue(result.isPresent(), "Business should be found by user ID.");
//        assertEquals(existingBusiness, result.get(), "The business returned should match the existing business.");
//    }
//
//    @Test
//    public void testDeleteBusiness() {
//        when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));
//        when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);
//
//        businessService.deleteBusiness(1L);
//
//        assertTrue(existingBusiness.isDeleted(), "The business should be marked as deleted.");
//    }
//}


//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.SCM.entities.Business;
//import com.SCM.repository.BusinessRepository;
//import com.SCM.service.BusinessService;
//import com.SCM.testLogAppender.TestLogAppender;
//
//import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//public class BusinessServiceTest {
//
//    @Mock
//    private BusinessRepository businessRepository;
//
//    @InjectMocks
//    private BusinessService businessService;
//
//    private Business newBusiness;
//    private Business existingBusiness;
//    private TestLogAppender testLogAppender;
//
//    @BeforeEach
//    public void setUp() {
//        newBusiness = new Business();
//        newBusiness.setId(null);
//        newBusiness.setName("New Business");
//
//        existingBusiness = new Business();
//        existingBusiness.setId(1L);
//        existingBusiness.setName("Sara");
//
//        testLogAppender = new TestLogAppender();
//        Logger logger = (Logger) LoggerFactory.getLogger(BusinessService.class);
//        logger.addAppender(testLogAppender);
//        testLogAppender.start();
//    }
//
//    @Test
//    public void updateExistingBusinessTest() {
//        // Arrange
//        when(businessRepository.findById(1L)).thenReturn(Optional.of(existingBusiness));
//        when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);
//
//        // Act
//        Business result = businessService.updateExistingBusiness(existingBusiness);
//
//        // Assert
//        try {
//            // Check if the name of the updated business is what you expect
//            assertEquals("Sara", result.getName());
//            System.out.println("Test passed: Business updated successfully.");
//        } catch (AssertionError e) {
//            // Print the error message if the assertion fails
//            System.err.println("Test failed: " + e.getMessage());
//            throw e;  // Ensure the test fails
//        } finally {
//            // Clear logs for the next test
//            testLogAppender.clear();
//        }
//    }

//}
