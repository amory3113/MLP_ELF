import java.io.Serializable;
import java.util.List;

public class Siec implements Serializable {
	private static final long serialVersionUID = 1L;
	Warstwa [] warstwy;
	int liczba_warstw;

	public Siec(int liczba_wejsc,int liczba_warstw,int [] lnww){
		this.liczba_warstw=liczba_warstw;
		warstwy=new Warstwa[liczba_warstw];
		for(int i=0;i<liczba_warstw;i++)
			warstwy[i]=new Warstwa((i==0)?liczba_wejsc:lnww[i-1],lnww[i]);
	}

	public void uczBatch(List<double[]> X, List<double[]> T, int epochs, double lr) {
		for (Warstwa w : warstwy)
			for (Neuron n : w.neurony)
				n.lr = lr;

		for (int e = 0; e < epochs; e++) {
			for (int k = 0; k < X.size(); k++) {
				double[] in = X.get(k);
				oblicz_wyjscie(in);

				Warstwa outL = warstwy[liczba_warstw - 1];
				for (int j = 0; j < outL.neurony.length; j++) {
					double yj = outL.y[j];
					outL.neurony[j].delta = (yj - T.get(k)[j]) * yj * (1 - yj);
				}

				for (int l = liczba_warstw - 2; l >= 0; l--) {
					warstwy[l].backHidden(warstwy[l + 1]);
				}

				double[] wej = in;
				for (Warstwa w : warstwy) {
					for (Neuron n : w.neurony) {
						n.akumuluj(wej);
					}
					wej = w.y;
				}
			}
			for (Warstwa w : warstwy)
				for (Neuron n : w.neurony)
					n.apply();
		}
	}
	
	double [] oblicz_wyjscie(double [] wejscia){
		double [] wyjscie=null;
		for(int i=0;i<liczba_warstw;i++)
			wejscia = wyjscie = warstwy[i].oblicz_wyjscie(wejscia);
		return wyjscie;
	}
}
