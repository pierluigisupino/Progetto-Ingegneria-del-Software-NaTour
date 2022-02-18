package com.ingsw2122_n_03.natour.whitebox;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.ingsw2122_n_03.natour.presentation.support.FormChecker;


public class PasswordTesterWhiteBox {

    private FormChecker checker;

    @Before
    public void setUp() {
        checker = new FormChecker();
    }


    @Test
    public void TestStatementOne() {
        assertFalse(checker.isPasswordValid("Pass1"));
    }

    @Test
    public void TestStatementTwo() {
        assertFalse(checker.isPasswordValid(" Password1"));
    }

    @Test
    public void TestStatementThree() {
        assertFalse(checker.isPasswordValid("passwordNotValid"));
    }






}
