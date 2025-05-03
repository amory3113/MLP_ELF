import java.io.Serializable;

public class Warstwa implements Serializable {
	private static final long serialVersionUID = 1L;
	Neuron [] neurony;
	int liczba_neuronow;
	public double[] y;

	public Warstwa(int liczba_wejsc,int liczba_neuronow){
		this.liczba_neuronow=liczba_neuronow;
		neurony=new Neuron[liczba_neuronow];
		for(int i=0;i<liczba_neuronow;i++)
			neurony[i]=new Neuron(liczba_wejsc);
	}
	public double[] oblicz_wyjscie(double[] wejscia) {
		y = new double[neurony.length];
		for (int i = 0; i < neurony.length; i++) {
			y[i] = neurony[i].oblicz_wyjscie(wejscia);
		}
		return y;
	}

    public void backHidden(Warstwa next) {
        for (int i = 0; i < neurony.length; i++) {
            double sum = 0.0;
            for (Neuron n : next.neurony) {
                sum += n.wagi[i + 1] * n.delta;
            }
            double yi = y[i];
            neurony[i].delta = yi * (1 - yi) * sum;
        }
    }
}
