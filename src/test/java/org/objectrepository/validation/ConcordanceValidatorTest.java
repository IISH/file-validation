package org.objectrepository.validation;

import org.junit.Test;

public class ConcordanceValidatorTest {

   @Test
    public void test() {
       ConcordanceValidator concordanceValidator = new ConcordanceValidator("C:\\temp\\10622", "ARCH00712", "10622");
              concordanceValidator.start();
   }

}
