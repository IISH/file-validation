#!/bin/bash
#
# Add any additional arguments and pass them to the instruction script.

java -cp /usr/bin/object-repository/concordanceValidatorMaven-1.0.jar org.objectrepository.validation.ConcordanceMain $@
