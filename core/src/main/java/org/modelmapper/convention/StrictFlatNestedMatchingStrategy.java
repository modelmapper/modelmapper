package org.modelmapper.convention;

import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.PropertyNameInfo;
import org.modelmapper.spi.Tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * See {@link MatchingStrategies#STRICT_FLAT_NESTED}.
 *
 * @author Dimitrije Mitić
 */
public class StrictFlatNestedMatchingStrategy implements MatchingStrategy {

    @Override
    public boolean matches(PropertyNameInfo propertyNameInfo) {
        List<Tokens> sourceTokens = propertyNameInfo.getSourcePropertyTokens();
        List<Tokens> destinationTokens = propertyNameInfo.getDestinationPropertyTokens();

        if (!Objects.equals(getFirstToken(sourceTokens), getFirstToken(destinationTokens))) {
            return false;
        }

        if (!getLastToken(sourceTokens).equals(getLastToken(destinationTokens))) {
            return false;
        }

        List<String> indexedSourceTokens = new ArrayList<>();
        List<String> indexedDestinationTokens = new ArrayList<>();

        int maxFirstLevelTokenSize = Math.max(sourceTokens.size(), destinationTokens.size());

        for (int firstLevelIdx = 0; firstLevelIdx < maxFirstLevelTokenSize; firstLevelIdx++) {
            Tokens sourceNestedTokens = getToken(sourceTokens, firstLevelIdx);
            Tokens destinationNestedTokens = getToken(destinationTokens, firstLevelIdx);

            int sourceNestedTokensSize = sourceNestedTokens.size();
            int destinationNestedTokensSize = destinationNestedTokens.size();

            int maxSizeNestedTokens = Math.max(sourceNestedTokensSize, destinationNestedTokensSize);

            for (int nestedIdx = 0; nestedIdx < maxSizeNestedTokens; nestedIdx++) {
                if(nestedIdx < sourceNestedTokensSize &&
                    (!areTokensMatchingAtIndexAndAdd(
                        indexedSourceTokens,
                        indexedDestinationTokens,
                        sourceNestedTokens.token(nestedIdx))
                    )
                ) {
                        return false;
                }

                if (nestedIdx < destinationNestedTokensSize &&
                    (!areTokensMatchingAtIndexAndAdd(
                        indexedDestinationTokens,
                        indexedSourceTokens,
                        destinationNestedTokens.token(nestedIdx))
                    )
                ) {
                        return false;

                }
            }
        }

        return indexedSourceTokens.size() == indexedDestinationTokens.size();
    }

    private boolean areTokensMatchingAtIndexAndAdd(List<String> tokensToAddTo,
                                                   List<String> tokensToCompareTo,
                                                   String value) {
        tokensToAddTo.add(value.toLowerCase());
        int index = tokensToAddTo.size() - 1;
        if (index < tokensToCompareTo.size()) {
            return tokensToAddTo.get(index).equals(tokensToCompareTo.get(index));
        }
        return true;
    }

    private String getFirstToken(List<Tokens> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }

        if (tokens.get(0).size() == 0) {
            return null;
        }

        return tokens.get(0).token(0).toLowerCase();
    }

    private String getLastToken(List<Tokens> tokens) {
        int tokensSize = tokens.size();
        Tokens token = tokens.get(tokensSize - 1);
        return token.token(token.size() - 1).toLowerCase();
    }

    private Tokens getToken(List<Tokens> tokens, int index) {
        try {
            return tokens.get(index);
        } catch (IndexOutOfBoundsException ex) {
            return Tokens.of();
        }
    }

    @Override
    public boolean isExact() {
        return true;
    }

    @Override
    public String toString() {
        return "StrictFlatNested";
    }
}
