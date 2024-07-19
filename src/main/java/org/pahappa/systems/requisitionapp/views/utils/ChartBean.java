package org.pahappa.systems.requisitionapp.views.utils;

import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@Component
@ViewScoped
public class ChartBean implements Serializable {

    private BarChartModel weeklyUsersModel;
    private PieChartModel pieModel;

    @Autowired
    private BudgetLineService budgetLineService;

    @PostConstruct
    public void init() {
        createBarModel();
        createPieModel();
    }

    public BarChartModel getWeeklyUsersModel() {
        return weeklyUsersModel;
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    private void createBarModel() {
        weeklyUsersModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Weekly Users");

        List<Number> values = Arrays.asList(65, 59, 80, 81, 56, 55, 40);
        barDataSet.setData(values);

        data.addChartDataSet(barDataSet);

        List<String> labels = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        data.setLabels(labels);

        weeklyUsersModel.setData(data);
    }

    private void createPieModel() {
        pieModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = Arrays.asList(20, 50, 100, 150);
        dataSet.setData(values);

        List<String> backgroundColors = Arrays.asList("rgb(255, 99, 132)", "rgb(54, 162, 235)", "rgb(255, 205, 86)", "rgb(75, 192, 192)");
        dataSet.setBackgroundColor(backgroundColors);

        data.addChartDataSet(dataSet);

        List<String> labels = Arrays.asList("Submitted", "Reviewed", "Approved", "Rejected");
        data.setLabels(labels);

        pieModel.setData(data);

    }
}
