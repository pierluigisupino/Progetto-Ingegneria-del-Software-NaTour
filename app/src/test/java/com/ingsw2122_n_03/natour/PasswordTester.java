package com.ingsw2122_n_03.natour;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ingsw2122_n_03.natour.presentation.support.FormChecker;

public class PasswordTester {

    private FormChecker checker = new FormChecker();

    @Test
    public void checkPasswordLengthLessThanMinimum() {
        assertFalse(checker.isPasswordValid("LE4GTh7"));
    }

    @Test
    public void checkPasswordLengthMoreThanMaximum() {
        assertFalse(checker.isPasswordValid("STRINGS_LUNCH1DISMISS1MA_SAENZ'S_SENS8"));
    }

    @Test
    public void checkPasswordWithSpaces() {
        assertFalse(checker.isPasswordValid(" String space123 "));
    }

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

    /**
     * LIMIT CASES
     * **/

    @Test
    public void checkPasswordLength8() {
        assertTrue(checker.isPasswordValid("Ad12345f"));
    }

    @Test
    public void checkPasswordLength20() {
        assertTrue(checker.isPasswordValid("Ad12345fksJ-du469dhJ"));
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

    /**
     * FULL VALID
     * **/

    @Test
    public void checkPasswordFullValid() {
        assertTrue(checker.isPasswordValid("ThisIsValid276"));
        assertTrue(checker.isPasswordValid("123one2THREE"));
    }

}
