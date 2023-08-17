Feature: Conduit

  Scenario Outline: Sign up API Test
    * Sign up with UserName:"<UserName>" and Email:"<Email>" and Password:"<Password>" failed

    Examples:
      | UserName  | Email               | Password  |
      | testuser1 | testuser1@gmail.com | password1 |
      | testuser2 | testuser2@gmail.com | password2 |

    Scenario Outline: Sign in API Test
      * Sign in with user "<UserName>" successful

      Examples:
        | UserName     |
        | demouser1001 |
        | demouser1002 |