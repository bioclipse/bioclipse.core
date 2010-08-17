/* 
 * Copyright (c) 2010  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package net.bioclipse.jasper.charCustomizers;

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;


/**
 * @author jonalv
 *
 */
public class DoseResponseChartCustomizer implements JRChartCustomizer {

    
    private static final Font normalsize 
        = new Font( "SansSerif", Font.PLAIN, 4 );
    private static final Font small 
        = new Font( "SansSerif", Font.PLAIN, 2 );
    
    @Override
    public void customize( JFreeChart chart, JRChart jasperchart ) {
        ValueAxis domainAxis = new LogarithmicAxis("Concentration");
        ValueAxis rangeAxis  = new NumberAxis("SI%");
        domainAxis.setTickLabelFont( small );
        rangeAxis.setTickLabelFont(  small );
        domainAxis.setLabelFont( normalsize );
        rangeAxis.setLabelFont( normalsize  );
        rangeAxis.setRange( 0, 110 );
        chart.getXYPlot().setDomainAxis( domainAxis );
        chart.getXYPlot().setRangeAxis(  rangeAxis  );
        chart.getXYPlot().setDomainGridlinesVisible( false );
    }
}
