import java.io.Serializable;
import java.util.Random;


public class Neuron implements Serializable {
	private static final long serialVersionUID = 1L;
	double [] wagi;
	int liczba_wejsc;
	private double[] dW;
	public double delta;
	public double lr = 0.05;

	public Neuron(int liczba_wejsc){
		this.liczba_wejsc=liczba_wejsc;
		wagi=new double[liczba_wejsc+1];
		generuj();
		dW = new double[wagi.length];
	}
	private void generuj() {
		Random r=new Random();
		for(int i=0;i<=liczba_wejsc;i++)
			wagi[i]=(r.nextDouble()-0.5)*2.0*10;
			//wagi[i]=(r.nextDouble()-0.5)*2.0*0.01;//do projektu
	}
	public double oblicz_wyjscie(double [] wejscia){
		double fi=wagi[0];
		//double fi=0.0;
		for(int i=1;i<=liczba_wejsc;i++)
			fi+=wagi[i]*wejscia[i-1];
		double wynik=1.0/(1.0+Math.exp(-fi));// funkcja aktywacji sigma -unip
		//double wynik=(fi>0.0)?1.0:0.0;//skok jednostkowy
		//double wynik=fi; //f.a. liniowa 
		return wynik;
	}

    public void akumuluj(double[] wej) {
        dW[0] += delta;
        for (int i = 1; i < wagi.length; i++) {
            dW[i] += delta * wej[i - 1];
        }
    }

    public void apply() {
        for (int i = 0; i < wagi.length; i++) {
            wagi[i] -= lr * dW[i];
            dW[i] = 0.0;
        }
    }
}
