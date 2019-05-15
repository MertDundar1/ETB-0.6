package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import javax.annotation.Nullable;




















































































@Beta
public final class BloomFilter<T>
  implements Predicate<T>, Serializable
{
  private final BloomFilterStrategies.BitArray bits;
  private final int numHashFunctions;
  private final Funnel<T> funnel;
  private final Strategy strategy;
  
  private BloomFilter(BloomFilterStrategies.BitArray bits, int numHashFunctions, Funnel<T> funnel, Strategy strategy)
  {
    Preconditions.checkArgument(numHashFunctions > 0, "numHashFunctions (%s) must be > 0", new Object[] { Integer.valueOf(numHashFunctions) });
    
    Preconditions.checkArgument(numHashFunctions <= 255, "numHashFunctions (%s) must be <= 255", new Object[] { Integer.valueOf(numHashFunctions) });
    
    this.bits = ((BloomFilterStrategies.BitArray)Preconditions.checkNotNull(bits));
    this.numHashFunctions = numHashFunctions;
    this.funnel = ((Funnel)Preconditions.checkNotNull(funnel));
    this.strategy = ((Strategy)Preconditions.checkNotNull(strategy));
  }
  





  public BloomFilter<T> copy()
  {
    return new BloomFilter(bits.copy(), numHashFunctions, funnel, strategy);
  }
  



  public boolean mightContain(T object)
  {
    return strategy.mightContain(object, funnel, numHashFunctions, bits);
  }
  




  @Deprecated
  public boolean apply(T input)
  {
    return mightContain(input);
  }
  











  public boolean put(T object)
  {
    return strategy.put(object, funnel, numHashFunctions, bits);
  }
  











  public double expectedFpp()
  {
    return Math.pow(bits.bitCount() / bitSize(), numHashFunctions);
  }
  

  @VisibleForTesting
  long bitSize()
  {
    return bits.bitSize();
  }
  














  public boolean isCompatible(BloomFilter<T> that)
  {
    Preconditions.checkNotNull(that);
    return (this != that) && (numHashFunctions == numHashFunctions) && (bitSize() == that.bitSize()) && (strategy.equals(strategy)) && (funnel.equals(funnel));
  }
  













  public void putAll(BloomFilter<T> that)
  {
    Preconditions.checkNotNull(that);
    Preconditions.checkArgument(this != that, "Cannot combine a BloomFilter with itself.");
    Preconditions.checkArgument(numHashFunctions == numHashFunctions, "BloomFilters must have the same number of hash functions (%s != %s)", new Object[] { Integer.valueOf(numHashFunctions), Integer.valueOf(numHashFunctions) });
    

    Preconditions.checkArgument(bitSize() == that.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", new Object[] { Long.valueOf(bitSize()), Long.valueOf(that.bitSize()) });
    

    Preconditions.checkArgument(strategy.equals(strategy), "BloomFilters must have equal strategies (%s != %s)", new Object[] { strategy, strategy });
    

    Preconditions.checkArgument(funnel.equals(funnel), "BloomFilters must have equal funnels (%s != %s)", new Object[] { funnel, funnel });
    

    bits.putAll(bits);
  }
  
  public boolean equals(@Nullable Object object)
  {
    if (object == this) {
      return true;
    }
    if ((object instanceof BloomFilter)) {
      BloomFilter<?> that = (BloomFilter)object;
      return (numHashFunctions == numHashFunctions) && (funnel.equals(funnel)) && (bits.equals(bits)) && (strategy.equals(strategy));
    }
    


    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(numHashFunctions), funnel, strategy, bits });
  }
  
  private static final Strategy DEFAULT_STRATEGY = ;
  
  @VisibleForTesting
  static final String USE_MITZ32_PROPERTY = "com.google.common.hash.BloomFilter.useMitz32";
  
  @VisibleForTesting
  static Strategy getDefaultStrategyFromSystemProperty()
  {
    return Boolean.parseBoolean(System.getProperty("com.google.common.hash.BloomFilter.useMitz32")) ? BloomFilterStrategies.MURMUR128_MITZ_32 : BloomFilterStrategies.MURMUR128_MITZ_64;
  }
  























  public static <T> BloomFilter<T> create(Funnel<T> funnel, int expectedInsertions, double fpp)
  {
    return create(funnel, expectedInsertions, fpp, DEFAULT_STRATEGY);
  }
  
  @VisibleForTesting
  static <T> BloomFilter<T> create(Funnel<T> funnel, int expectedInsertions, double fpp, Strategy strategy)
  {
    Preconditions.checkNotNull(funnel);
    Preconditions.checkArgument(expectedInsertions >= 0, "Expected insertions (%s) must be >= 0", new Object[] { Integer.valueOf(expectedInsertions) });
    
    Preconditions.checkArgument(fpp > 0.0D, "False positive probability (%s) must be > 0.0", new Object[] { Double.valueOf(fpp) });
    Preconditions.checkArgument(fpp < 1.0D, "False positive probability (%s) must be < 1.0", new Object[] { Double.valueOf(fpp) });
    Preconditions.checkNotNull(strategy);
    
    if (expectedInsertions == 0) {
      expectedInsertions = 1;
    }
    





    long numBits = optimalNumOfBits(expectedInsertions, fpp);
    int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
    try {
      return new BloomFilter(new BloomFilterStrategies.BitArray(numBits), numHashFunctions, funnel, strategy);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Could not create BloomFilter of " + numBits + " bits", e);
    }
  }
  















  public static <T> BloomFilter<T> create(Funnel<T> funnel, int expectedInsertions)
  {
    return create(funnel, expectedInsertions, 0.03D);
  }
  






















  @VisibleForTesting
  static int optimalNumOfHashFunctions(long n, long m)
  {
    return Math.max(1, (int)Math.round(m / n * Math.log(2.0D)));
  }
  








  @VisibleForTesting
  static long optimalNumOfBits(long n, double p)
  {
    if (p == 0.0D) {
      p = Double.MIN_VALUE;
    }
    return (-n * Math.log(p) / (Math.log(2.0D) * Math.log(2.0D)));
  }
  

  private Object writeReplace() { return new SerialForm(this); }
  
  private static class SerialForm<T> implements Serializable {
    final long[] data;
    final int numHashFunctions;
    final Funnel<T> funnel;
    final BloomFilter.Strategy strategy;
    private static final long serialVersionUID = 1L;
    
    SerialForm(BloomFilter<T> bf) {
      data = bits.data;
      numHashFunctions = numHashFunctions;
      funnel = funnel;
      strategy = strategy;
    }
    
    Object readResolve() { return new BloomFilter(new BloomFilterStrategies.BitArray(data), numHashFunctions, funnel, strategy, null); }
  }
  
  static abstract interface Strategy
    extends Serializable
  {
    public abstract <T> boolean put(T paramT, Funnel<? super T> paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
    
    public abstract <T> boolean mightContain(T paramT, Funnel<? super T> paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
    
    public abstract int ordinal();
  }
}
