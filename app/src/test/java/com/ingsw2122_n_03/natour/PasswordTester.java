package com.ingsw2122_n_03.natour;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ingsw2122_n_03.natour.presentation.support.FormChecker;

public class PasswordTester {

    FormChecker checker = new FormChecker();

    @Test
    public void checkPasswordLengthLessThanMinimum() {
        assertFalse(checker.isPasswordValid("Ad12"));
        assertFalse(checker.isPasswordValid("AAa.15f"));
        assertFalse(checker.isPasswordValid(""));
        assertFalse(checker.isPasswordValid("LE4GTh7"));
    }

    @Test
    public void checkPasswordLengthMoreThanMaximum() {
        assertFalse(checker.isPasswordValid("STRINGS_LUNCH1DISMISS1MA_SAENZ'S_SENS8"));
        assertFalse(checker.isPasswordValid("length-EXACTLY--21--:"));
    }

    @Test
    public void checkPasswordWithSpaces() {
        assertFalse(checker.isPasswordValid(" STARTIN_space123"));
        assertFalse(checker.isPasswordValid("FINAL_space123 "));
        assertFalse(checker.isPasswordValid(" OUTER_space123 "));
        assertFalse(checker.isPasswordValid("MIDDLE_ - spaces1"));
        assertFalse(checker.isPasswordValid(" _Spaces 123 "));
    }

    @Test
    public void checkPasswordWithoutNumber() {
        assertFalse(checker.isPasswordValid("NO-number-here"));
        assertFalse(checker.isPasswordValid("NotEVEN-here"));
    }

    @Test
    public void checkPasswordWithoutUpperCase() {
        assertFalse(checker.isPasswordValid("no-caps-her3"));
        assertFalse(checker.isPasswordValid("not1even-here"));
    }

    @Test
    public void checkPasswordWithoutLowerCase() {
        assertFalse(checker.isPasswordValid("NO-LOWER.HER3"));
        assertFalse(checker.isPasswordValid("NOT3VE4HER3"));
    }

    /**
     * LIMIT CASES
     * **/

    @Test
    public void checkPasswordLength8() {
        assertTrue(checker.isPasswordValid("Ad12345f"));
        assertTrue(checker.isPasswordValid("D1a_.gS0"));
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
        assertTrue(checker.isPasswordValid("ThisIsValid123"));
        assertTrue(checker.isPasswordValid("MyPassword0"));
        assertTrue(checker.isPasswordValid("passwordM7"));
        assertTrue(checker.isPasswordValid("123one2THREE"));
    }

}
