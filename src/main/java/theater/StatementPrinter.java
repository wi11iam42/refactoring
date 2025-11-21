package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private Invoice invoice;
    private Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        final StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer()
                + System.lineSeparator());

        for (Performance p : invoice.getPerformances()) {

            // add volume credits
            volumeCredits += getVolumeCredits(p);
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", getPlay(p).getName(),
                    usd(getAmount(p)), p.audience));
            totalAmount += getAmount(p);
        }
        result.append(String.format("Amount owed is %s%n",
                usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private static String usd(int totalAmount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(totalAmount / Constants.PERCENT_FACTOR);
    }

    private int getVolumeCredits(Performance performance) {
        int result = 0;
        result += Math.max(performance.audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.audience / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    private Play getPlay(Performance performance) {
        return plays.get(performance.playID);
    }

    private int getAmount(Performance perf) {
        int thisAmount = 0;
        switch (getPlay(perf).getType()) {
            case "tragedy":
                thisAmount = Constants.PASTORAL_BASE_AMOUNT;
                if (perf.audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (perf.audience - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (perf.audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (perf.audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * perf.audience;
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", getPlay(perf).getType()));
        }
        return thisAmount;
    }
}
