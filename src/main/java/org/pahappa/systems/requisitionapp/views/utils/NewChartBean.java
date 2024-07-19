package org.pahappa.systems.requisitionapp.views.utils;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
@ViewScoped
public class NewChartBean implements Serializable {

    @Autowired
    private UserService userService;

    @Autowired
    private BudgetLineService budgetLineService;

    private BarChartModel weeklyUsersModel;
    private PieChartModel pieModel;
    private PieChartModel pieModel2;
    private List<User> users;
    private List<BudgetLine> budgetLines;

    @PostConstruct
    public void init() {
        createPieModel2();
        createPieModel();
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
}
