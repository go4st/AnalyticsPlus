/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hswt.anap.service.rulesolver.impl.rules;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;
import de.hswt.anap.model.ValidationResult.Priority;
import de.hswt.anap.service.rulesolver.impl.Rule;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.HplcSimulator;
import de.hswt.hplcsl.Results;

public class BackPressureRule implements Rule {

	private final static String MESSAGE_ERROR = "Backpressure is too high! Must be lower or equal 400.";

	private static final String MESSAGE_HELP = "If you're having trouble finding matching items, you can look for help at our moodle webpage!";

	private static final String MESSAGE_VALID = "Backpressure is acceptable.";

	private HplcSimulator hplcSimulator;

	public BackPressureRule() {
		hplcSimulator = new HplcSimulator();
	}

	@Override
	public List<ValidationResult> validate(Experiment experiment) {
		List<ValidationResult> result = new ArrayList<>();

		if (experiment.getFlowRate() == null
				|| experiment.getInjectionVolume() == null
				|| experiment.getParticleSize() == null
				|| experiment.getColumnLength() == null
				|| !experiment.getSolvent().isPresent()) {
			// TODO improve this message, maybe tell what is missing
			throw new InvalidParameterException(
					"Experiment is not complete for this rule.");
		}

		Definition definition = experiment.getDefinition();

		Results calculatorResult = hplcSimulator.calculateParameter(definition);

		evaluateResult(calculatorResult, result);

		return result;
	}

	private void evaluateResult(Results results,
			List<ValidationResult> validationResults) {

		if (results.getBackpressure() > 400) {
			validationResults.add(new ValidationResult(results
					.getBackpressure(), false, MESSAGE_ERROR, MESSAGE_HELP,
					Priority.ERROR));
		} else {

			validationResults.add(new ValidationResult(results
					.getBackpressure(), true, MESSAGE_VALID, Priority.RESULT));
		}
	}

	@Override
	public RuleType getRuleType() {
		return RuleType.BACK_PRESSURE;
	}

}
