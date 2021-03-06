/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link Moh711ReportBuilder}
 */
public class Moh711ReportBuilderTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	@Qualifier("kenyaemr.common.report.moh711")
	private ReportDescriptor report;

	@Autowired
	private Moh711ReportBuilder builder;

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();

		regimenManager.refresh();
	}

	@Test
	public void test() throws Exception {
		ReportDefinition rd = builder.build(report);

		// Run report on all patients for June 2012
		EvaluationContext context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		//ReportingTestUtils.printReport(data);

		Assert.assertThat(data.getDataSets().size(), is(2));
		Assert.assertThat(data.getDataSets(), hasKey("G"));
		Assert.assertThat(data.getDataSets(), hasKey("K"));

		// Check that report definition can be serialized/de-serialized
		Context.getService(ReportDefinitionService.class).saveDefinition(rd);
		Context.getService(ReportDefinitionService.class).getDefinition(rd.getId());
	}
}