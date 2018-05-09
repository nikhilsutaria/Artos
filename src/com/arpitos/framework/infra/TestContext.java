package com.arpitos.framework.infra;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;

import com.arpitos.framework.Enums.TestStatus;
import com.arpitos.framework.Static_Store;

/**
 * This is TestContext which is wrapper around all objects/tools/loggers user
 * may need during test case execution. This class is also responsible for
 * Summarising test results.
 * 
 * @author ArpitS
 *
 */
public class TestContext {

	private OrganisedLog organiseLogger;
	private TestStatus currentTestStatus = TestStatus.PASS;
	private boolean KnownToFail = false;
	private long testStartTime;
	private String strJIRARef = "";
	private long totalTestCount = 0;
	private long currentPassCount = 0;
	private long currentFailCount = 0;
	private long currentSkipCount = 0;
	private long currentKTFCount = 0;
	Map<String, Object> globalObjectsHashMap = new HashMap<String, Object>();

	/**
	 * Constructor
	 * 
	 * @param organisedLog
	 *            = Logger object
	 */
	public TestContext(OrganisedLog organisedLog) {
		this.organiseLogger = organisedLog;
		printMendatoryInfo();
		Static_Store.SysProperties.printUsefulInfo(organisedLog.getGeneralLogger());
	}

	/**
	 * Prints Organisation details to each log files
	 */
	private void printMendatoryInfo() {
		//@formatter:off
		
		String organisationInfo = "************************************ Header Start ******************************************"
								 +"\nOrganisation_Name : " + Static_Store.FWConfig.getOrganisation_Name()
								 +"\nOrganisation_Country : " + Static_Store.FWConfig.getOrganisation_Country()
								 +"\nOrganisation_Address : " + Static_Store.FWConfig.getOrganisation_Address()
								 +"\nOrganisation_Phone : " + Static_Store.FWConfig.getOrganisation_Contact_Number()
								 +"\nOrganisation_Email : " + Static_Store.FWConfig.getOrganisation_Email()
								 +"\nOrganisation_Website : " + Static_Store.FWConfig.getOrganisation_Website()
								 +"\n************************************ Header End ********************************************";
		getOrganiseLogger().getGeneralLogger().info(Banner.getBanner());
		getOrganiseLogger().getGeneralLogger().info(organisationInfo);

		getOrganiseLogger().getSummaryLogger().info(Banner.getBanner());
		getOrganiseLogger().getSummaryLogger().info(organisationInfo);
		//@formatter:on
	}

	/**
	 * Sets Test status in memory. Status is not finalized until
	 * generateTestSummary() function is called. This function stamps "FAIL
	 * HERE" warning as soon as status is set to FAIL so user can pin point
	 * location of the failure
	 * 
	 * @param testStatus
	 *            = Test Status
	 */
	public void setCurrentTestStatus(TestStatus testStatus) {
		if (testStatus.getValue() >= currentTestStatus.getValue()) {
			currentTestStatus = testStatus;

			// Append Warning in the log so user can pin point where test failed
			if (testStatus == TestStatus.FAIL) {
				//@formatter:off
				getLogger().warn("**********************************"
								+"\n*********** FAIL HERE ************"
								+"\n**********************************");
				//@formatter:on
			}
		}
	}

	/**
	 * Concludes final test result and generates summary report. This also
	 * includes bugTicketNumber if provided
	 * 
	 * @param strTestName
	 *            = Test Name
	 */
	public void generateTestSummary(String strTestName) {
		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (isKnownToFail()) {
			if (getCurrentTestStatus() == TestStatus.PASS) {
				//@formatter:off
				getLogger().warn("**********************************"
								+"\n******** KTF TEST PASSED *********"
								+"\n**********************************");
				//@formatter:on
				setCurrentTestStatus(TestStatus.FAIL);
			}
		}

		// Add to total test count
		setTotalTestCount(getTotalTestCount() + 1);

		// Store count details per status
		if (getCurrentTestStatus() == TestStatus.PASS) {
			setCurrentPassCount(getCurrentPassCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.FAIL) {
			setCurrentFailCount(getCurrentFailCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentSkipCount(getCurrentSkipCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentKTFCount(getCurrentKTFCount() + 1);
		}

		long totalTestTime = System.currentTimeMillis() - getTestStartTime();
		// Finalise and add test result in log file
		getLogger().info("Test Result : " + getCurrentTestStatus().name());
		// Finalise and add test summary to Summary report
		getOrganiseLogger().appendSummaryReport(getCurrentTestStatus(), strTestName, getStrJIRARef(), getCurrentPassCount(), getCurrentFailCount(),
				getCurrentSkipCount(), getCurrentKTFCount(), totalTestTime);

		// reset statuses for next test
		resetTestStatus();
		setKnownToFail(false, "");
	}

	private void resetTestStatus() {
		// Reset for next test
		currentTestStatus = TestStatus.PASS;
	}

	/**
	 * Get the method name for a depth in call stack.
	 * 
	 * <PRE>
	 * depth in the call stack 
	 * 0 = current method
	 * 1 = call method
	 * etc..
	 * </PRE>
	 * 
	 * @return method name
	 */
	public String printMethodName() {
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		getLogger().debug("\nMethod : " + methodName + "()");
		return methodName;
	}

	public Logger getLogger() {
		return (Logger) getOrganiseLogger().getGeneralLogger();
	}

	public TestStatus getCurrentTestStatus() {
		return currentTestStatus;
	}

	public boolean isKnownToFail() {
		return KnownToFail;
	}

	public void setKnownToFail(boolean knownToFail, String strJIRARef) {
		KnownToFail = knownToFail;
		setStrJIRARef(strJIRARef);
		setTestStartTime(System.currentTimeMillis());
	}

	private String getStrJIRARef() {
		return strJIRARef;
	}

	private void setStrJIRARef(String strJIRARef) {
		this.strJIRARef = strJIRARef;
	}

	/**
	 * Sets Object which is available globally to all test cases. User must
	 * maintain Key for the HashTable
	 * 
	 * @param key
	 *            = Key to recognize an Object
	 * @param obj
	 *            = Object to be stored
	 */
	public void setGlobalObject(String key, Object obj) {
		globalObjectsHashMap.put(key, obj);
	}

	/**
	 * Gets Globally set Object from the Map using provided Key.
	 * 
	 * @param key
	 *            = String key to retrive an object
	 * @return
	 */
	public Object getGlobalObject(String key) {
		return globalObjectsHashMap.get(key);
	}

	public long getCurrentPassCount() {
		return currentPassCount;
	}

	private void setCurrentPassCount(long currentPassCount) {
		this.currentPassCount = currentPassCount;
	}

	public long getCurrentFailCount() {
		return currentFailCount;
	}

	private void setCurrentFailCount(long currentFailCount) {
		this.currentFailCount = currentFailCount;
	}

	public long getCurrentSkipCount() {
		return currentSkipCount;
	}

	private void setCurrentSkipCount(long currentSkipCount) {
		this.currentSkipCount = currentSkipCount;
	}

	public long getCurrentKTFCount() {
		return currentKTFCount;
	}

	private void setCurrentKTFCount(long currentKTFCount) {
		this.currentKTFCount = currentKTFCount;
	}

	public long getTotalTestCount() {
		return totalTestCount;
	}

	private void setTotalTestCount(long totalTestCount) {
		this.totalTestCount = totalTestCount;
	}

	public OrganisedLog getOrganiseLogger() {
		return organiseLogger;
	}

	public void setOrganiseLogger(OrganisedLog organiseLogger) {
		this.organiseLogger = organiseLogger;
	}

	public long getTestStartTime() {
		return testStartTime;
	}

	public void setTestStartTime(long testStartTime) {
		this.testStartTime = testStartTime;
	}

}