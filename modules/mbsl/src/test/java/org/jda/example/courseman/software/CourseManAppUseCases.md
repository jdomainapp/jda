CourseMan application
----------------------------

## Use Case 1
Activity diagram description:

1. Student profile registration
2. Student enrolment registration
3. Decisional: HelpOrSClassWithPayment
   1. HelpRequest: ask for help about the next steps to perform
   2. Fork: SClassWithPayment: perform sclass registration and payment processing concurrently
      1. SClassRegistration: students register into morning, afternoon, evening classes
      2. Fork: PaymentProcessing: process student payment for enrolment
         1. Payment: make enrolment payment
         2. Authorisation: authorise registration

## Use Case 2
Use case 1 plus Join and merge

1. Student profile registration
2. Student enrolment registration
3. Decision (HelpOrSClassWithPayment)
   1. Out1: HelpRequest: ask for help about the next steps to perform
   2. Out2: Fork (SClassWithPayment): perform sclass registration and payment processing concurrently
      1. Out1: SClassRegistration: students register into morning, afternoon, evening classes
      2. Out2: Fork (PaymentProcessing): process student payment for enrolment
         1. Out1: Payment: make enrolment payment
         2. Out2: Authorisation: authorise registration
      3. Join (EnrolmentApproval): approve student enrolment
         1. In1: Payment
         2. In2: Authorisation
4. Merge: EnrolmentAndOrOrientation
   1. In1: Join (EnrolmentApproval)
   2. In2: Orientation
   3. Out: Complete Enrolment
5. Complete Enrolment