package com.example.encryption;

public class Random extends java.util.Random   {

    public Random(long seed) {
        super(seed);
    }




    public int nextInt(int origin, int bound){

        int r = nextInt();
        if (origin < bound) {
            // It's not case (1).
            final int n = bound - origin;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = nextInt() >>> 1);
                r += origin;
            }
            else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound) {
                    r = nextInt();
                }
            }
        }
        return r;

    }

}
