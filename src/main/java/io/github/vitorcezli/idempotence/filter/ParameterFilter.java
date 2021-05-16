package io.github.vitorcezli.idempotence.filter;

import java.util.ArrayList;
import java.util.List;

public class ParameterFilter {

    public static Object[] filter(
            final List<String> includes,
            final List<String> excludes,
            final List<String> parameterNames,
            final Object[] args
    ) throws ParameterFilterException {
        if (containsFilterValues(includes) && containsFilterValues(excludes)) {
            final String message = "cannot both includes and excludes be defined on the same function";
            throw new ParameterFilterException(message);
        }
        if (containsFilterValues(includes)) {
            return processInclusion(includes, parameterNames, args);
        }
        if (containsFilterValues(excludes)) {
            return processExclusion(excludes, parameterNames, args);
        }

        return args;
    }

    private static Object[] processInclusion(
            final List<String> filterValues,
            final List<String> parameters,
            final Object[] args
    ) throws ParameterFilterException {
        assertFilterValuesAreValid(filterValues, parameters);

        final List<Object> selectedArgs = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if (filterValues.contains(parameters.get(i))) {
                selectedArgs.add(args[i]);
            }
        }

        return selectedArgs.toArray();
    }

    private static Object[] processExclusion(
            final List<String> filterValues,
            final List<String> parameters,
            final Object[] args
    ) throws ParameterFilterException {
        assertFilterValuesAreValid(filterValues, parameters);

        final List<Object> selectedArgs = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if (!filterValues.contains(parameters.get(i))) {
                selectedArgs.add(args[i]);
            }
        }

        return selectedArgs.toArray();
    }

    private static void assertFilterValuesAreValid(
            final List<String> filterValues,
            final List<String> parameters
    ) throws ParameterFilterException {
        for (final String filterValue : filterValues) {
            if (!parameters.contains(filterValue)) {
                final String message = "filter '" + filterValue + "' on include/exclude "
                        + "has no correspondence with function parameters";
                throw new ParameterFilterException(message);
            }
        }
    }

    private static boolean containsFilterValues(final List<String> filterValues) {
        return 1 != filterValues.size() || !"".equals(filterValues.get(0));
    }
}
