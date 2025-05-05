# hw2

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTrackerApp
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.

## Features
- Add a new transaction: First specify the amount and category. Then click on the Add transaction button. Adds the new transaction to the list and updates the total cost.
- Filter the transaction list by either amount or category: First specify the amount or category to be matched. Then click the corresponding Filter button. Highlights the matching transactions in the list.
- Undo Transaction: Click the Undo Transaction button and it will remove the last added transaction. If any filter is applied when the undo functionality is ran then it
will remove the filter so the user can see all of the current transactions.