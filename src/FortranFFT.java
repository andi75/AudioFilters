
public class FortranFFT {
    public static void rfft(float X[],int N)
    {
	int I,I0,I1,I2,I3,I4,I5,I6,I7,I8, IS,ID;
	int J,K,M,N2,N4,N8;
	float A,A3,CC1,SS1,CC3,SS3,E,R1,XT;
	float T1,T2,T3,T4,T5,T6;

	M=(int)(Math.log(N)/Math.log(2.0));               /* N=2^M */

	/* ----Digit reverse counter--------------------------------------------- */
	J = 1;
	for(I=1;I<N;I++)
	{
        if (I<J)
		{
			XT    = X[J];
			X[J]  = X[I];
			X[I]  = XT;
		}
        K = N/2;
        while(K<J)
		{
			J -= K;
			K /= 2;
		}
        J += K;
	}

	/* ----Length two butterflies--------------------------------------------- */
	IS = 1;
	ID = 4;
	do
	{
        for(I0 = IS;I0<=N;I0+=ID)
		{
			I1    = I0 + 1;
			R1    = X[I0];
			X[I0] = R1 + X[I1];
			X[I1] = R1 - X[I1];
		}
        IS = 2 * ID - 1;
        ID = 4 * ID;
	}while(IS<N);
	/* ----L shaped butterflies----------------------------------------------- */
	N2 = 2;
	for(K=2;K<=M;K++)
	{
        N2    = N2 * 2;
        N4    = N2/4;
        N8    = N2/8;
        E     = (float) 6.2831853071719586f/N2;
        IS    = 0;
        ID    = N2 * 2;
        do
		{
			for(I=IS;I<N;I+=ID)
			{
				I1 = I + 1;
				I2 = I1 + N4;
				I3 = I2 + N4;
				I4 = I3 + N4;
				T1 = X[I4] +X[I3];
				X[I4] = X[I4] - X[I3];
				X[I3] = X[I1] - T1;
				X[I1] = X[I1] + T1;
				if(N4!=1)
				{
					I1 += N8;
					I2 += N8;
					I3 += N8;
					I4 += N8;
					T1 = (X[I3] + X[I4])*.7071067811865475244f;
					T2 = (X[I3] - X[I4])*.7071067811865475244f;
					X[I4] = X[I2] - T1;
					X[I3] = -X[I2] - T1;
					X[I2] = X[I1] - T2;
					X[I1] = X[I1] + T2;
				}
			}
			IS = 2 * ID - N2;
			ID = 4 * ID;
		}while(IS<N);
        A = E;
        for(J= 2;J<=N8;J++)
		{
			A3 = 3.0f * A;
			CC1   = (float) Math.cos(A);
			SS1   = (float) Math.sin(A);  /*typo A3--really A?*/
			CC3   = (float) Math.cos(A3); /*typo 3--really A3?*/
			SS3   = (float) Math.sin(A3);
			A = (float)J * E;
			IS = 0;
			ID = 2 * N2;
			do
			{
				for(I=IS;I<N;I+=ID)
				{
					I1 = I + J;
					I2 = I1 + N4;
					I3 = I2 + N4;
					I4 = I3 + N4;
					I5 = I + N4 - J + 2;
					I6 = I5 + N4;
					I7 = I6 + N4;
					I8 = I7 + N4;
					T1 = X[I3] * CC1 + X[I7] * SS1;
					T2 = X[I7] * CC1 - X[I3] * SS1;
					T3 = X[I4] * CC3 + X[I8] * SS3;
					T4 = X[I8] * CC3 - X[I4] * SS3;
					T5 = T1 + T3;
					T6 = T2 + T4;
					T3 = T1 - T3;
					T4 = T2 - T4;
					T2 = X[I6] + T6;
					X[I3] = T6 - X[I6];
					X[I8] = T2;
					T2    = X[I2] - T3;
					X[I7] = -X[I2] - T3;
					X[I4] = T2;
					T1    = X[I1] + T5;
					X[I6] = X[I1] - T5;
					X[I1] = T1;
					T1    = X[I5] + T4;
					X[I5] = X[I5] - T4;
					X[I2] = T1;
				}
				IS = 2 * ID - N2;
				ID = 4 * ID;
			}while(IS<N);
		}
	}
	return;
    }
}
