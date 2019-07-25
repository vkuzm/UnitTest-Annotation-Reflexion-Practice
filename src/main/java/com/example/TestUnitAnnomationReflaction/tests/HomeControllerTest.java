package com.example.TestUnitAnnomationReflaction.tests;


import com.example.TestUnitAnnomationReflaction.HomeController;
import com.example.TestUnitAnnomationReflaction.annotations.*;

@TestEnabled
public class HomeControllerTest {

    private void test() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        System.out.println(name);
    }

    private HomeController homeController;
    private int totalSuccess;

    @BeforeClass
    public void setUpClass() {
        System.out.println("BEFORE CLASS");
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("AFTER CLASS");
    }

    @Before
    public void setUp() {
        System.out.println("BEFORE TEST");
        homeController = new HomeController();
    }

    @After
    public void tearDown() {
        System.out.println("AFTER TEST");
        homeController = null;
    }

    @Test
    public void HomeControllerNotNull() {
        if (homeController != null) {
            test();
            totalSuccess++;
        }
    }

    @Test
    public void HomePageAccessable() {
        if (homeController.index() != null) {
            System.out.println("TEST PASSED!");
        } else {
            throw new RuntimeException("Controller does't exist");
        }
    }

    @Test
    public void HomePageHasContent() {
        if (homeController.index().equals("Home Page")) {
            System.out.println("TEST PASSED!");
        } else {
            throw new RuntimeException("Content isn't equal");
        }
    }

}