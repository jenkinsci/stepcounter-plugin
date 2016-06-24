package org.jenkinsci.plugins.stepcounter;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jenkinsci.plugins.stepcounter.model.StepCounterResult;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.Run;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

public class StepCounterResultAction implements Action {

    /**
     * カテゴリごとのステップ数集計結果。キーはカテゴリ名
     */
    Map<String, StepCounterResult> _stepsMap = new HashMap<String, StepCounterResult>();

    private AbstractBuild<?, ?> owner;

    private Map<Integer, Color> _colors = new HashMap<Integer, Color>();

    public StepCounterResultAction(AbstractBuild<?, ?> build) {
        this.owner = build;

        for (Field field : Color.class.getDeclaredFields()) {
            if (field.getClass().equals(Color.class)) {
                try {
                    Color color = (Color) field.get(null);
                    if (color != null && !_colors.containsKey(color.getRGB())) {
                        _colors.put(color.getRGB(), color);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getDisplayName() {
        return "StepCounterResultAction";
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getUrlName() {
        return "stepCounterResult";
    }

    public Map<String, StepCounterResult> getStepsMap() {
        return this._stepsMap;
    }

    public void putStepsMap(String key, StepCounterResult result) {
        _stepsMap.put(key, result);
    }

    private String getRelPath(StaplerRequest req) {
        String relPath = req.getParameter("rel");
        if (relPath == null)
            return "";
        return relPath;
    }

    public void createClickableMap(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Graph graph = createDefaultGraph(req, rsp);
        graph.doMap(req, rsp);
    }

    public void createGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Graph graph = createDefaultGraph(req, rsp);
        graph.doPng(req, rsp);
    }

    private Graph createDefaultGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        final String relPath = getRelPath(req);

        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return null;
        }

        AbstractBuild<?, ?> build = getBuild();
        Calendar t = Calendar.getInstance();
        if(build != null){
            t = build.getTimestamp();
        }

        String w = Util.fixEmptyAndTrim(req.getParameter("width"));
        String h = Util.fixEmptyAndTrim(req.getParameter("height"));
        int width = (w != null) ? Integer.valueOf(w) : 500;
        int height = (h != null) ? Integer.valueOf(h) : 200;

        Graph graph = new GraphImpl(this, t, width, height, relPath) {

            @Override
            protected DataSetBuilder<String, NumberOnlyBuildLabel> createDataSet(StepCounterResultAction obj) {
                DataSetBuilder<String, NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, NumberOnlyBuildLabel>();

                for (StepCounterResultAction a = obj; a != null; a = a.getPreviousResult()) {
                    Map<String, StepCounterResult> stepsMap = a.getStepsMap();
                    NumberOnlyBuildLabel label = new NumberOnlyBuildLabel((Run<?,?>)a.getBuild());
                    for (Entry<String, StepCounterResult> entry : stepsMap.entrySet()) {
                        dsb.add(entry.getValue().getTotalSum(), entry.getKey(), label);
                    }
                }
                return dsb;
            }
        };
        return graph;
    }

    private AbstractBuild<?, ?> getBuild() {
        return this.owner;
    }

    private abstract class GraphImpl extends Graph {

        private StepCounterResultAction obj;
        private String relPath;

        public GraphImpl(StepCounterResultAction obj, Calendar timestamp, int defaultW, int defaultH, String relPath) {
            super(timestamp, defaultW, defaultH);
            this.obj = obj;
            this.relPath = relPath;
        }

        protected abstract DataSetBuilder<String, NumberOnlyBuildLabel> createDataSet(StepCounterResultAction obj);

        protected JFreeChart createGraph() {
            final CategoryDataset dataset = createDataSet(obj).build();
            final JFreeChart chart = ChartFactory.createLineChart(null, // chart
                    // title
                    null, // unused
                    "Steps", // range axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );

            // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

            final LegendTitle legend = chart.getLegend();
            legend.setPosition(RectangleEdge.RIGHT);

            chart.setBackgroundPaint(Color.white);

            final CategoryPlot plot = chart.getCategoryPlot();

            // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0,
            // 5.0));
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlinePaint(null);
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlinePaint(Color.black);

            CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
            plot.setDomainAxis(domainAxis);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
            domainAxis.setLowerMargin(0.0);
            domainAxis.setUpperMargin(0.0);
            domainAxis.setCategoryMargin(0.0);

            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setAutoRange(true);
            // rangeAxis.setLowerBound(0);

            // final LineAndShapeRenderer renderer = (LineAndShapeRenderer)
            // plot.getRenderer();
            // renderer.setBaseStroke(new BasicStroke(4.0f));
            // ColorPalette.apply(renderer);

            @SuppressWarnings("serial")
            StackedAreaRenderer ar = new StackedAreaRenderer2() {
                @Override
                public String generateURL(CategoryDataset dataset, int row, int column) {
                    NumberOnlyBuildLabel label = (NumberOnlyBuildLabel) dataset.getColumnKey(column);
                    return relPath + label.build.getNumber() + "/"
                            + StepCounterProjectAction.STEPCOUNTERPROJECTACTION_PATH + "/";
                }

                @Override
                public String generateToolTip(CategoryDataset dataset, int row, int column) {
                    NumberOnlyBuildLabel label = (NumberOnlyBuildLabel) dataset.getColumnKey(column);
                    StepCounterProjectAction a = label.build.getAction(StepCounterProjectAction.class);
                    Map<String, StepCounterResult> stepsMap = a.getResult().getStepsMap();
                    Comparable<?> key = dataset.getRowKey(row);
                    for (Entry<String, StepCounterResult> entry : stepsMap.entrySet()) {
                        if (entry.getKey().equals(key)) {
                            return "[" + entry.getKey() + "] ビルド " + label.build.getDisplayName() + " 合計:"
                                    + entry.getValue().getTotalSum() + " 実行:" + entry.getValue().getRunsSum()
                                    + " コメント:" + entry.getValue().getCommentsSum() + " 空行:"
                                    + entry.getValue().getBlanksSum();
                        }
                    }

                    return "不明";
                }
            };
            plot.setRenderer(ar);

            for (int i = 0; i < ar.getColumnCount(); i++) {
                ar.setSeriesPaint(i, _colors.get(_colors.values().toArray()[i]));
            }
            // ar.setSeriesPaint(0, ColorPalette.BLUE);
            // ar.setSeriesPaint(1, ColorPalette.YELLOW);
            // ar.setSeriesPaint(2, ColorPalette.GREY); //TODO
            // ar.setSeriesPaint(3, Color.black);

            // crop extra space around the graph
            plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

            return chart;
        }
    }

    public StepCounterResultAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    private StepCounterResultAction getPreviousResult(AbstractBuild<?, ?> start) {
    	if(start ==null) return null;
        AbstractBuild<?, ?> b = start;
        while (true) {
            b = b.getPreviousBuild();
            if (b == null)
                return null;
            if (b.getResult() == Result.FAILURE)
                continue;
            StepCounterProjectAction r = b.getAction(StepCounterProjectAction.class);
            if (r != null)
                return r.getResult();
        }
    }

    public AbstractBuild<?, ?> getOwner(){
        return this.owner;
    }

}
