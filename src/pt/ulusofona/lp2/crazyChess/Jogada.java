package pt.ulusofona.lp2.crazyChess;


public class Jogada implements Comparable<Jogada> {
    int x , y , nPontos;

    Jogada(int x, int y, int nPontos){
        this.x = x;
        this.y = y;
        this.nPontos = nPontos;
    }

    @Override
    public String toString() {
        return this.x + ", " + this.y + ", " + this.nPontos;
    }

    public int compareTo(Jogada jogada){
        if(jogada.nPontos > 0){
            if(this.nPontos > jogada.nPontos){
                return 1;
            }else{
                return -1;
            }
        }
        return 0;
    }
}
