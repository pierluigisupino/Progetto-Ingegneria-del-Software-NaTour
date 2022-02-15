package com.ingsw2122_n_03.natour;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ingsw2122_n_03.natour.presentation.support.FormChecker;

public class PasswordTester {

    private final FormChecker checker = new FormChecker();


    /**
     * LENGTH
     * **/

    @Test
    public void checkPasswordLengthZero() {
        assertFalse(checker.isPasswordValid(""));
    }

    @Test
    public void checkPasswordLengthLessThanMinimum() {
        assertFalse(checker.isPasswordValid("LE4GTh7"));
    }

    @Test
    public void checkPasswordLengthEight() {
        assertTrue(checker.isPasswordValid("Ad123f-8"));
    }

    @Test
    public void checkPasswordLengthMiddle() {
        assertTrue(checker.isPasswordValid("Ad12345fPi_w14"));
    }

    @Test
    public void checkPasswordLengthTwenty() {
        assertTrue(checker.isPasswordValid("Ad12345fksJ-du469dhJ"));
    }

    @Test
    public void checkPasswordLengthMoreThanMaximum() {
        assertFalse(checker.isPasswordValid("STRINGS_lengthA231.21"));
    }


    /**
     * SPACES
     * **/

    @Test
    public void checkPasswordWithoutSpaces() {
        assertTrue(checker.isPasswordValid("MyPassword123"));
    }

    @Test
    public void checkPasswordWithOneSpace() {
        assertFalse(checker.isPasswordValid("String space123"));
    }

    @Test
    public void checkPasswordWithMultipleSpaces() {
        assertFalse(checker.isPasswordValid(" String - space123 "));
    }


    /**
     * REGEX
     * **/

    @Test
    public void checkPasswordWithoutNumber() {
        assertFalse(checker.isPasswordValid("NO-number-here"));
    }

    @Test
    public void checkPasswordWithoutUpperCase() {
        assertFalse(checker.isPasswordValid("no-caps-her3"));
    }

    @Test
    public void checkPasswordWithoutLowerCase() {
        assertFalse(checker.isPasswordValid("NO-LOWER.HER3"));
    }


    @Test
    public void checkPasswordOneNumeric() {
        assertTrue(checker.isPasswordValid("1-NumberHere"));
    }

    @Test
    public void checkPasswordOneUpper() {
        assertTrue(checker.isPasswordValid("One-upper-h3r3"));
    }

    @Test
    public void checkPasswordOneLower() {
        assertTrue(checker.isPasswordValid("oNE-LOWER-H3R3"));
    }


}
