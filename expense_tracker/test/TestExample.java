// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  public static final String CATEGORY_FOOD = "food";
  public static final String CATEGORY_ENTERTAINMENT = "entertainment";  
    
  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = CATEGORY_FOOD;
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = CATEGORY_FOOD;
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    // The test suite has an existing test case for removing a transaction contained in the transaction list. You
    // need to modify this test suite to add a new test case trying to remove a transaction NOT contained in the
    // transaction list.
    @Test
    public void testUndoTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        controller.undoTransaction(); // Attempt to undo a transaction (aka a transaction NOT contained in the transaction list). This shouldn't error

        assertEquals(0, model.getTransactions().size()) // This should equal 0 as nothing has changed
    }
        
    @Test
    public void testInvalidInputHandling() {
	// This is new test case 2: For the Controller
	//
	// Check pre-conditions
	assertEquals(0, model.getTransactions().size());
	assertEquals(0.00, getTotalCost(), 0.01);
	// Call the unit under test
        boolean didAddTransaction = controller.addTransaction(0.00, "InvalidCategory");
	// Check post-conditions (i.e. nothing changed)
	assertFalse(didAddTransaction);
        assertEquals(0, model.getTransactions().size());
        assertEquals(0.00, getTotalCost(), 0.01);

	// See above for the pre-conditions
	//
	// Call the unit under test
	boolean didAddTransaction2 = controller.addTransaction(50.00, "");
	// Check the post-conditions
	assertFalse(didAddTransaction2);
        assertEquals(0, model.getTransactions().size());
        assertEquals(0.00, getTotalCost(), 0.01);
    }

    //filter by amount
    @Test
    public void testFilterByAmount() {
	// This is new test case 3: For the Model
	//
	// Setup
	double[] amountsList = { 50.0, 30.0, 40.0 };
	double amountToFilterBy = amountsList[0];
	String[] categoriesList = { CATEGORY_FOOD, CATEGORY_ENTERTAINMENT, CATEGORY_FOOD };
	for (int i = 0; i < amountsList.length; i++) {
	    controller.addTransaction(amountsList[i], categoriesList[i]);
	}

	// Check pre-conditions
	List<Transaction> transactionsList = model.getTransactions();
	assertEquals(3, transactionsList.size());
	for (int j = 0; j < transactionsList.size(); j++) {
	    Transaction currentTransaction = transactionsList.get(j);
	    checkTransaction(amountsList[j], categoriesList[j], currentTransaction);
	}

	// Call unit under test
        controller.setFilter(new AmountFilter(amountToFilterBy));
        controller.applyFilter();

	// Check the post-conditions
        List<Transaction> displayedTransactions = view.getDisplayedTransactions();
        assertEquals(1, displayedTransactions.size());
        assertEquals(amountToFilterBy, displayedTransactions.get(0).getAmount(), 0.01);
    } 


    //filter by category
    @Test
    public void testFilterByCategory() {
	// This is new test case 4: For the Model
	//
        // Setup
	double[] amountsList = { 50.0, 30.0, 40.0 };
        String[] categoriesList = { CATEGORY_FOOD, CATEGORY_ENTERTAINMENT, CATEGORY_FOOD };
	String categoryToFilterBy = categoriesList[0];
        for (int i = 0; i < amountsList.length; i++) {
            controller.addTransaction(amountsList[i], categoriesList[i]);
        }

        // Check pre-conditions 
        List<Transaction> transactionsList = model.getTransactions();
        assertEquals(3, transactionsList.size());
        for (int j = 0; j < transactionsList.size(); j++) {
            Transaction currentTransaction = transactionsList.get(j);
            checkTransaction(amountsList[j], categoriesList[j], currentTransaction);
        }
	
	// Call the unit under test
        controller.setFilter(new CategoryFilter(categoryToFilterBy));
        controller.applyFilter();

	// Check the post-conditions
        List<Transaction> displayedTransactions = view.getDisplayedTransactions();
        assertEquals(2, displayedTransactions.size());
	for (Transaction currDisplayedTransaction : displayedTransactions) {
	    assertEquals(categoryToFilterBy, currDisplayedTransaction.getCategory());
	}
    }
}
