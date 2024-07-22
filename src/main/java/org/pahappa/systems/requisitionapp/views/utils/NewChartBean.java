package org.pahappa.systems.requisitionapp.views.utils;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.AccountabilityStatus;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@ViewScoped
public class NewChartBean implements Serializable {

    @Autowired
    private UserService userService;

    @Autowired
    private BudgetLineService budgetLineService;

    @Autowired
    private RequisitionService requisitionService;

    private BarChartModel weeklyUsersModel;
    private PieChartModel pieModel;
    private PieChartModel pieModel2;
    private BarChartModel requisitionFrequencyModel;
    private BarChartModel accountabilityModel;

    private List<User> users;
    private List<BudgetLine> budgetLines;

    @PostConstruct
    public void init() {
        femaleUsersCount();
        maleUsersCount();
        draftBudgetLinesCount();
        expiredBudgetLinesCount();
        approvedBudgetLinesCount();
        rejectedBudgetLinesCount();
        accountedRequisitionsCount();
        unAccountedRequisitionsCount();

        createPieModel2();
        createPieModel();
        createRequisitionFrequencyModel();
        createAccountabilityModel();
    }

    public void createPieModel(){
        pieModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(maleUsersCount());
        values.add(femaleUsersCount());
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        labels.add("Male");
        labels.add("Female");
        data.setLabels(labels);

        pieModel.setData(data);
    }

    public void createPieModel2(){
        pieModel2 = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(draftBudgetLinesCount());
        values.add(approvedBudgetLinesCount());
        values.add(expiredBudgetLinesCount());
        values.add(rejectedBudgetLinesCount());
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        bgColors.add("rgb(75, 192, 192)");  // A teal/cyan color
        bgColors.add("rgb(255, 159, 64)");  // An orange color
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        labels.add("Draft");
        labels.add("Approved");
        labels.add("Expired");
        labels.add("Rejected");
        data.setLabels(labels);

        pieModel2.setData(data);
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public PieChartModel getPieModel2() {
        return pieModel2;
    }

    public int femaleUsersCount(){
        List<User> users = userService.getAllUsers();
        List<BudgetLine> budgetLines = budgetLineService.getAllBudgetLines();
        List<User> females = new ArrayList<>();
        for (User user : users){
            if(user.getGender().equals(Gender.FEMALE)){
                females.add(user);
            }
        }
        return females.size();
    }

    public int maleUsersCount(){
        List<User> users = userService.getAllUsers();
        List<BudgetLine> budgetLines = budgetLineService.getAllBudgetLines();
        List<User> males = new ArrayList<>();
        for (User user : users){
            if(user.getGender().equals(Gender.MALE)){
                males.add(user);
            }
        }
        return males.size();
    }

    public int draftBudgetLinesCount(){
        List<User> users = userService.getAllUsers();
        List<BudgetLine> budgetLines = budgetLineService.getAllBudgetLines();
        List<BudgetLine> draftBudgetLines = new ArrayList<>();
        for (BudgetLine budgetLine : budgetLines){
            if (budgetLine.getStatus().equals(BudgetLineStatus.DRAFT)){
                draftBudgetLines.add(budgetLine);
            }
        }
        return draftBudgetLines.size();
    }

    public int approvedBudgetLinesCount(){
        List<User> users = userService.getAllUsers();
        List<BudgetLine> budgetLines = budgetLineService.getAllBudgetLines();
        List<BudgetLine> draftBudgetLines = new ArrayList<>();
        for (BudgetLine budgetLine : budgetLines){
            if (budgetLine.getStatus().equals(BudgetLineStatus.APPROVED)){
                draftBudgetLines.add(budgetLine);
            }
        }
        return draftBudgetLines.size();
    }

    public int expiredBudgetLinesCount(){
        List<User> users = userService.getAllUsers();
        List<BudgetLine> budgetLines = budgetLineService.getAllBudgetLines();
        List<BudgetLine> draftBudgetLines = new ArrayList<>();
        for (BudgetLine budgetLine : budgetLines){
            if (budgetLine.getStatus().equals(BudgetLineStatus.EXPIRED)){
                draftBudgetLines.add(budgetLine);
            }
        }
        return draftBudgetLines.size();
    }

    public int rejectedBudgetLinesCount(){
        List<User> users = userService.getAllUsers();
        List<BudgetLine> budgetLines = budgetLineService.getAllBudgetLines();
        List<BudgetLine> draftBudgetLines = new ArrayList<>();
        for (BudgetLine budgetLine : budgetLines){
            if (budgetLine.getStatus().equals(BudgetLineStatus.REJECTED)){
                draftBudgetLines.add(budgetLine);
            }
        }
        return draftBudgetLines.size();
    }

    public int accountedRequisitionsCount(){
        List<Requisition> requisitions = requisitionService.getAllRequisitions();
        List<Requisition> accountedRequisitions = new ArrayList<>();
        for (Requisition requisition : requisitions){
            if (requisition.getAccountability() != null){
                if (requisition.getAccountability().getStatus().equals(AccountabilityStatus.APPROVED)){
                    accountedRequisitions.add(requisition);
                }
            }
        }
        return accountedRequisitions.size();
    }

    public int unAccountedRequisitionsCount(){
        List<Requisition> requisitions = requisitionService.getAllRequisitions();
        List<Requisition> unAccountedRequisitions = new ArrayList<>();
        for (Requisition requisition : requisitions){
            if (requisition.getAccountability() == null || !requisition.getAccountability().getStatus().equals(AccountabilityStatus.APPROVED)){
                unAccountedRequisitions.add(requisition);
            }
        }
        return unAccountedRequisitions.size();
    }

    public void createRequisitionFrequencyModel() {
        requisitionFrequencyModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet creationDataSet = new BarChartDataSet();
        creationDataSet.setLabel("Submitted");
        List<Number> creationValues = new ArrayList<>();

        BarChartDataSet disbursementDataSet = new BarChartDataSet();
        disbursementDataSet.setLabel("Disbursed");
        List<Number> disbursementValues = new ArrayList<>();

        Map<String, Long> creationFrequency = requisitionService.getCreationFrequencyByDayOfWeek();
        Map<String, Long> disbursementFrequency = requisitionService.getDisbursementFrequencyByDayOfWeek();

        List<String> labels = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        for (String day : labels) {
            creationValues.add(creationFrequency.getOrDefault(day, 0L));
            disbursementValues.add(disbursementFrequency.getOrDefault(day, 0L));
        }

        creationDataSet.setData(creationValues);
        creationDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        creationDataSet.setBorderColor("rgb(255, 99, 132)");
        creationDataSet.setBorderWidth(1);

        disbursementDataSet.setData(disbursementValues);
        disbursementDataSet.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        disbursementDataSet.setBorderColor("rgb(54, 162, 235)");
        disbursementDataSet.setBorderWidth(1);

        data.addChartDataSet(creationDataSet);
        data.addChartDataSet(disbursementDataSet);
        data.setLabels(labels);

        requisitionFrequencyModel.setData(data);

        // Customize the chart
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setBeginAtZero(true);
        ticks.setStepSize(1);
        ticks.setPrecision(0);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Requisition Frequency by Day of Week");
        options.setTitle(title);

        requisitionFrequencyModel.setOptions(options);
    }

    public BarChartModel getRequisitionFrequencyModel() {
        return requisitionFrequencyModel;
    }

    public void createAccountabilityModel(){
        accountabilityModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet dataset = new BarChartDataSet();
        dataset.setLabel("Requisitions");
        List<Number> values = new ArrayList<>();
        values.add(accountedRequisitionsCount());
        values.add(unAccountedRequisitionsCount());
        dataset.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgba(255, 99, 132, 0.2)");
        bgColors.add("rgba(54, 162, 235, 0.2)");
        dataset.setBackgroundColor(bgColors);

        List<String> borderColors = new ArrayList<>();
        borderColors.add("rgb(255, 99, 132)");
        borderColors.add("rgb(54, 162, 235)");
        dataset.setBorderColor(borderColors);

        dataset.setBorderWidth(1);

        data.addChartDataSet(dataset);

        List<String> labels = Arrays.asList("Accounted Requisitions", "Unaccounted Requisitions");
        data.setLabels(labels);

        accountabilityModel.setData(data);

        // Customize the chart
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setBeginAtZero(true);
        ticks.setStepSize(1);
        ticks.setPrecision(0);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Accounted for and unaccounted for requisitions");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(false);  // Hide the legend since it's not needed
        options.setLegend(legend);

        accountabilityModel.setOptions(options);
    }

    public BarChartModel getAccountabilityModel() {
        return accountabilityModel;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<BudgetLine> getBudgetLines() {
        return budgetLines;
    }

    public void setBudgetLines(List<BudgetLine> budgetLines) {
        this.budgetLines = budgetLines;
    }

    public void refreshChartData(){
        femaleUsersCount();
        maleUsersCount();
        draftBudgetLinesCount();
        expiredBudgetLinesCount();
        approvedBudgetLinesCount();
        rejectedBudgetLinesCount();
        accountedRequisitionsCount();
        unAccountedRequisitionsCount();

        createPieModel();
        createPieModel2();
        createRequisitionFrequencyModel();
        createAccountabilityModel();
    }

}
