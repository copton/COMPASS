package pdf;

/**
 * encapsulation of a probability
 */
public class Probability {
    public Probability(double prob)
    {
        this.prob = prob;
    }

    public Probability scale(Probability factor)
    {
        prob *= factor.prob;

        return this;
    }

    public Probability reciprocal()
    {
        prob = 1.0 / prob;
        return this;
    }

    public int compareTo(Probability p)
    {
        if (prob > p.prob) return 1;
        if (prob < p.prob) return -1;
        return 0;
    }

    public Probability add(Probability amount)
    {
        prob += amount.prob;
        
        return this;
    }

    public double getValue()
    {
        return prob;
    }

    private double prob;
}
