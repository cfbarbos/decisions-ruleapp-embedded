package com.ibm.decisions.loanvalidation;


import java.util.Map;

import ilog.rules.res.model.IlrPath;
import ilog.rules.res.session.IlrJ2SESessionFactory;
import ilog.rules.res.session.IlrSessionRequest;
import ilog.rules.res.session.IlrSessionResponse;
import ilog.rules.res.session.IlrStatelessSession;
import ilog.rules.res.session.config.IlrJDBCPersistenceConfig;
import ilog.rules.res.session.config.IlrPersistenceConfig;
import ilog.rules.res.session.config.IlrPersistenceType;
import ilog.rules.res.session.config.IlrSessionFactoryConfig;
import ilog.rules.res.session.config.IlrXUConfig;
import miniloan.Borrower;
import miniloan.Loan;

public class LoanValidationRESRunner {
	
	

	private static IlrJ2SESessionFactory GetRuleSessionFactory() {
		
		IlrSessionFactoryConfig factoryConfig = IlrJ2SESessionFactory.createDefaultConfig();
	
		IlrXUConfig xuConfig = factoryConfig.getXUConfig();
		xuConfig.setLogAutoFlushEnabled(false);
		xuConfig.getPersistenceConfig().setPersistenceType(IlrPersistenceType.MEMORY);
		xuConfig.getManagedXOMPersistenceConfig().setPersistenceType(IlrPersistenceType.MEMORY);
		//return GetRuleSessionFactoryJDBC();
		return new IlrJ2SESessionFactory(factoryConfig);
	}
	
	private static IlrJ2SESessionFactory GetRuleSessionFactoryJDBC() {

		IlrSessionFactoryConfig config = IlrJ2SESessionFactory.createDefaultConfig();
		IlrPersistenceConfig persistenceConfig = config.getXUConfig().getPersistenceConfig();
		IlrPersistenceType persistenceType = IlrPersistenceType.JDBC;
		persistenceConfig.setPersistenceType(persistenceType);
		IlrJDBCPersistenceConfig jdbcPersistenceConfig = config.getXUConfig().getPersistenceConfig().getJDBCPersistenceConfig();
		//jdbcPersistenceConfig.setDriverClassName("com.ibm.db2.jcc.DB2Driver")
		jdbcPersistenceConfig.setDriverClassName("org.h2.Driver");
		jdbcPersistenceConfig.setPassword("ilog");
		//jdbcPersistenceConfig.setURL( "jdbc:db2://HOST[:PORT]/DB");
		jdbcPersistenceConfig.setURL( "jdbc:h2:C:\\IBM\\wlp-21.0.0.9\\usr\\servers\\odm81101\\data\\h2\\resdb");
		jdbcPersistenceConfig.setUser("ilog");
		

		
		return new IlrJ2SESessionFactory(config);

		}


	public static void main(String[] args) {
		LoanValidationRESRunner runner = new LoanValidationRESRunner();
		Borrower borrower = new Borrower("Smith", 1000, 20);
		borrower.setCreditScore(800);
		borrower.setYearlyIncome(100000);
		
		Loan loan = new Loan( 48, 20000, 0.05);
		
		IlrSessionResponse response = runner.execute(borrower, loan);
		Map<String, Object> outParameters = response.getOutputParameters();
		Loan loan2 = (Loan) outParameters.get("loan");
		
	    System.out.println(loan2.getApprovalStatus());
	    
	}
	
	
	public IlrSessionResponse execute(Borrower borrower, Loan loan) {
		try {
			IlrJ2SESessionFactory sessionFactory =  GetRuleSessionFactory(); //ou GetRuleSessionFactoryJDBC()
			IlrSessionRequest sessionRequest = sessionFactory.createRequest();
			String rulesetPath = "/tutorial_ruleapp/miniloan_ruleset";
			sessionRequest.setRulesetPath(IlrPath.parsePath(rulesetPath));
			
			//sessionRequest.setTraceEnabled(true);
			//sessionRequest.getTraceFilter().setInfoAllFilters(true);
			//sessionRequest.getTraceFilter().setInfoRules(true);
			//sessionRequest.getTraceFilter().setInfoRulesNotFired(true);
			//sessionRequest.getTraceFilter().setInfoTasks(true);
			//sessionRequest.getTraceFilter().setInfoTotalTasksNotExecuted(true);
			//sessionRequest.getTraceFilter().setInfoExecutionEvents(true);

			Map<String, Object> inputParameters = sessionRequest.getInputParameters();
			inputParameters.put("loan", loan);
			inputParameters.put("borrower", borrower);


			IlrStatelessSession session = sessionFactory.createStatelessSession();
			IlrSessionResponse response = session.execute(sessionRequest);
			return response;

		} catch (Exception exception) {
			exception.printStackTrace(System.err);
		}
		return null;
	}

}