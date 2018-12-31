package pt.ulusofona.lp2.crazyChess;

import java.util.List;

public class TorreH extends CrazyPiece {
    public TorreH(int id,int equipa,String alcunha){
        super.id=id;
        super.tipoPeca=4;
        super.equipa=equipa;
        super.alcunha=alcunha;
        super.valorRelativo="3";
        super.passoMax=Simulador.tamanhoTabuleiro;
        if(equipa == 10){
            imagePNG = "torreh_preta.png";
        }else if(equipa == 20){
            imagePNG = "torreh_branca.png";
        }
    }

    @Override
    public boolean podeMover(int x, int y){
        CrazyPiece novaPeace = Simulador.receberPeca(x,y);

        if(y-this.getPosY()==0 && this.getPosX() != x ){
            for(int mov=1;mov<Math.abs(x-this.getPosX());mov++){
                if((x-this.getPosX())<0){
                    novaPeace=Simulador.receberPeca(this.getPosX()-mov,y);
                }
                else if((x-this.getPosX())>0){
                    novaPeace=Simulador.receberPeca(this.getPosX()+mov,y);
                }

                if(novaPeace!=null){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}