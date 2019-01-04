package pt.ulusofona.lp2.crazyChess;

import javax.xml.soap.SOAPPart;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulador {

//Variaveis que seram mudadas de lugar
    private int validasPretas = 0,capturadasPretas = 0,invalidasPretas = 0; // Equipa a jogar - 0
    private int validasBrancas = 0,capturadasBrancas = 0,invalidasBrancas = 0; //Equipa a jogar - 1
    private int vencedor,semCaptura = 0;
    private boolean capturaPrevia = false, antigaCapturaPrevia;
    private int turnoAntigo,capturasAntigas;
    int equipaJogar,countAnulaJogada=0,turno = 0,tamanhoTabuleiro;

    //Listas
    List<CrazyPiece> pecasMalucas = new ArrayList<>();
    List<String> recuperaPecas = new ArrayList<>();
    private List<String> informacaoEquipas = new ArrayList<>();

 //Contrutores
    public Simulador(){
    }

    public Simulador(int boardSize) {
        tamanhoTabuleiro = boardSize;
    }

//Leitura do Ficheiro (Feito)
    public boolean iniciaJogo(File ficheiroInicial){
        this.reset();
        try{
            Scanner leitorFicheiro = new Scanner (ficheiroInicial);
            int countLinha=0;
            int nPecas = 0;

            /*
            primeira info-> dimensões do tabuleiro
            segunda info-> n peças
            terceira info-> caracterização de peças
            quarta info-> localização das peças no tabuleiro
             */

            while(leitorFicheiro.hasNextLine()){

                String linha= leitorFicheiro.nextLine();
                String info[];

                if(countLinha == 0){
                    if(Integer.parseInt(linha)>=4 && Integer.parseInt(linha)<=12) {
                        tamanhoTabuleiro = Integer.parseInt(linha); //guarda o tamanho do tabuleiro
                    }else{
                        return false;
                    }

                }else if(countLinha==1){
                    if(Integer.parseInt(linha)<(tamanhoTabuleiro*tamanhoTabuleiro)) {
                        nPecas = Integer.parseInt(linha); //guarda o numero de pecas
                    }else{
                        return false;
                    }

                }else if((countLinha-2)<nPecas){
                    info=linha.split(":"); //coloca num array a infomacao da linha e é separada pelos :
                    CrazyPiece novaPeca=null;
                    int tipoP=Integer.parseInt(info[1]);

                    switch (tipoP){
                        case 0:
                            novaPeca=new Rei(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3]);
                            break;

                        case 1:
                            novaPeca=new Rainha(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3]);
                            break;

                        case 2:
                            novaPeca=new PoneiMagico(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3]);
                            break;

                        case 3:
                            novaPeca=new PadreDaVila(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3]);
                            break;

                        case 4:
                            novaPeca=new TorreH(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3],tamanhoTabuleiro);
                            break;

                        case 5:
                            novaPeca=new TorreV(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3],tamanhoTabuleiro);
                            break;

                        case 6:
                            novaPeca=new Lebre(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3]);
                            break;

                        case 7:
                            novaPeca=new Joker(Integer.parseInt(info[0]),Integer.parseInt(info[2]),info[3],turno);
                            break;

                        default:
                            break;
                    }
                    pecasMalucas.add(novaPeca);

                }else if((countLinha-nPecas-2)<tamanhoTabuleiro){
                    info=linha.split(":");
                    for(int i=0;i < tamanhoTabuleiro;i++){
                        if(Integer.parseInt(info[i]) != 0){
                            for(CrazyPiece crazyPiece:pecasMalucas){
                                if(Integer.parseInt(info[i]) == crazyPiece.getId()){
                                    crazyPiece.estaEmJogo();
                                    crazyPiece.setPosicao(i,countLinha-(nPecas+2));
                                }
                            }
                        }
                    }
                }else{
                    info = linha.split(":");
                    validasPretas = Integer.parseInt(info[1]);
                    capturadasPretas = Integer.parseInt(info[2]);
                    invalidasPretas = Integer.parseInt(info[3]);
                    validasBrancas = Integer.parseInt(info[4]);
                    capturadasBrancas = Integer.parseInt(info[5]);
                    invalidasBrancas = Integer.parseInt(info[6]);
                    turno=validasBrancas + validasPretas;
                }

                countLinha++;
            }

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }

//Envia o tamanho do tabuleiro (Feito)
    public int getTamanhoTabuleiro(){
        return tamanhoTabuleiro;
    }

//Executa o movimento de uma peça (Resolver o problema da horizontal)
    public boolean processaJogada(int xO, int yO, int xD, int yD){
        //Guardar a posição e estado das peças
        recuperaPecas.clear();
        String linha;
        for(CrazyPiece peace:pecasMalucas){
            linha = "" + peace.getId() + ":" + peace.getPosX() + ":" + peace.getPosY();
            recuperaPecas.add(linha);
        }
        System.out.println(recuperaPecas);

        //Apaga a informação existente anteriormente
        informacaoEquipas.clear();

        //Guardar a informação da Equipa Preta
        informacaoEquipas.add(Integer.toString(validasPretas));
        informacaoEquipas.add(Integer.toString(capturadasPretas));
        informacaoEquipas.add(Integer.toString(invalidasPretas));

        //Guardar a informação da Equipa Branca
        informacaoEquipas.add(Integer.toString(validasBrancas));
        informacaoEquipas.add(Integer.toString(capturadasBrancas));
        informacaoEquipas.add(Integer.toString(invalidasBrancas));

        //Guardar a informação relativa ao jogo
        turnoAntigo = turno;
        capturasAntigas=semCaptura;
        antigaCapturaPrevia=capturaPrevia;

        if(((xO>=0 && xO<tamanhoTabuleiro) && (yO>=0 && yO<tamanhoTabuleiro)) &&
                ((xD>=0 && xD<tamanhoTabuleiro) && (yD>=0 && yD<tamanhoTabuleiro))){
            CrazyPiece origem=receberPeca(xO,yO,pecasMalucas);

            if(origem!=null && origem.getEquipa()==this.getIDEquipaAJogar()){
                CrazyPiece destino=receberPeca(xD,yD,pecasMalucas);
                if (destino == null) {
                    if (origem.podeMover(xD,yD,pecasMalucas,turno,tamanhoTabuleiro)){
                        origem.setPosicao(xD,yD);
                        this.semCaptura++;
                        if(this.getIDEquipaAJogar()==10){ //Pretas
                            this.validasPretas++;
                        }else if(this.getIDEquipaAJogar()==20){ //Brancas
                            this.validasBrancas++;
                        }
                        turno++;
                        System.out.println(recuperaPecas);
                        return true;
                    }
                } else if (!destino.equipaEquals(equipaJogar)) {
                    if (origem.podeMover(xD,yD,pecasMalucas,turno,tamanhoTabuleiro)){
                        destino.setPosicao(-1,-1);
                        origem.setPosicao(xD,yD);
                        this.semCaptura=0;
                        this.capturaPrevia=true;
                        if(this.getIDEquipaAJogar()==10){ //Pretas
                            this.validasPretas++;
                            this.capturadasPretas++;
                        }else if(this.getIDEquipaAJogar()==20){ //Brancas
                            this.validasBrancas++;
                            this.capturadasBrancas++;
                        }
                        turno++;
                        System.out.println(recuperaPecas);
                        return true;
                    }
                }
            }
        }
        //contar jogada invalida
        if(this.getIDEquipaAJogar()==10){ //Pretas
            invalidasPretas++;
        }else if(this.getIDEquipaAJogar()==20){ //Brancas
            invalidasBrancas++;
        }
        System.out.println("invalida");
        return false;
    }

//Devolve a lista de todas asa peças em jogo(Feito)
    public List<CrazyPiece> getPecasMalucas(){
        return pecasMalucas;
    }

//Premite finalizar o jogo se for comprida alguma das condições (Alterar)
    public boolean jogoTerminado(){
        int reisBrancos = 0,reisPretos = 0;
        if (pecasMalucas == null){
            reset();
            return true;
        }
        for(CrazyPiece piece:pecasMalucas){
            if(piece.getEmJogo()) {
                if (!piece.comida()) {
                    if (piece.getTipoPeca() == 0) {
                        if (piece.getEquipa() == 10) { //Pecas Pretas
                            reisPretos++;
                        } else if (piece.getEquipa() == 20) { //Pecas Brancas
                            reisBrancos++;
                        }
                    }
                }
            }
        }
        //Ver vencedor:
        //0 - Pretas
        //1 - Brancas
        //2 - Empate

        if(reisBrancos==0 && reisPretos!=0){
            //PRETAS VENCEM
            this.vencedor=0;
            return true;
        }else if(reisPretos==0 && reisBrancos!=0){
            //BRANCAS VENCEM
            this.vencedor=1;
            return true;
        }else if((reisBrancos==1 && reisPretos==1) || (reisBrancos==0 && reisPretos==0)){
            //EMPATE
            this.vencedor=2;
            return true;
        }

        if(this.capturaPrevia){
            if(semCaptura==10){
                this.vencedor=2;
                return true;
            }
        }

        return false;
    }

//Devolve a lista dos autores (Feito)
    public List<String> getAutores(){
        List<String> autores=new ArrayList<>();
        autores.add("Ana Maria - nº 21703436");
        autores.add("Diana Jesus - nº 21703012");
        return autores;
    }

//Devolve o valor dos resultados do jogo
    public List<String> getResultados(){
        List<String> resultados=new ArrayList<>();
        //Ver vencedor:
        //0 - Pretas
        //1 - Brancas
        //2 - Empate

        resultados.add("JOGO DE CRAZY CHESS");
        switch (this.vencedor){
            case 0:{
                resultados.add("Resultado: VENCERAM AS PRETAS");
                break;}

            case 1:{
                resultados.add("Resultado: VENCERAM AS BRANCAS");
                break;}

            case 2:{
                resultados.add("Resultado: EMPATE");
                break;}
        }
        resultados.add("---");

        //Informacao da equipa preta
        resultados.add("Equipa das Pretas");
        resultados.add("Capturas: " + this.capturadasPretas);
        resultados.add("Jogadas válidas: " + this.validasPretas);
        resultados.add("Tentativas inválidas: " + this.invalidasPretas);

        //Informacao da equipa branca
        resultados.add("Equipa das Brancas");
        resultados.add("Capturas: " + this.capturadasBrancas + "");
        resultados.add("Jogadas válidas: " + this.validasBrancas + "");
        resultados.add("Tentativas inválidas: " + this.invalidasBrancas + "");
        return resultados;
    }

//Devolve o id de uma peça naquela posição (Feito)
    public int getIDPeca(int x, int y){
        for(CrazyPiece crazyPiece:pecasMalucas) {
            if (crazyPiece.getPosX()==x && crazyPiece.getPosY()==y) {
                return crazyPiece.getId();
            }
        }
        return 0;
    }

//Devolve a equipa a jogar (Feito)
    public int getIDEquipaAJogar(){
        if(turno%2==0){
            equipaJogar=10; //Pretas
        }else{
            equipaJogar=20; //Brancas
        }
        return equipaJogar;
    }

    public static int getEquipaJogar(int turno){
        if(turno%2==0){
            return 10; //Pretas
        }else{
            return 20; //Brancas
        }
    }

//Devolve a peça que se encontra numa determinada coordenada
    public static CrazyPiece receberPeca(int x,int y,List<CrazyPiece> pecasMalucas){

        for(CrazyPiece piece: pecasMalucas) {
            if (piece.posX == x && piece.posY== y) {
                return piece;
            }
        }
        return null;
    }

//Disponibiliza as possíveis jogadas de cada peça
    public List<String> obterSugestoesJogada(int xO, int yO){
        List<String> sugetoesJogada = new ArrayList<>();
        CrazyPiece peace = receberPeca(xO,yO,pecasMalucas);
        if (peace != null) {
            sugetoesJogada = peace.sugetaoJogada(xO,yO,pecasMalucas,turno,tamanhoTabuleiro);
        }
        return sugetoesJogada;
    }

//Premite anular a ultima jogada realizada
    public void anularJogadaAnterior(){
        if(countAnulaJogada<=1) {
            int count = 0;
            //Repor as peças
            for (String linha : recuperaPecas) {
                String info[];
                info = linha.split(":");
                for (CrazyPiece peace : pecasMalucas) {
                    if (peace.getId() == Integer.parseInt(info[0])) {
                        int x = Integer.parseInt(info[1]);
                        int y = Integer.parseInt(info[2]);
                        peace.setPosicao(x, y);
                    }
                }
            }

            for (String equipas : informacaoEquipas) {
//Repor a informação da equipa Preta
                if (count == 0) {
                    validasPretas = Integer.parseInt(equipas);
                } else if (count == 1) {
                    capturadasPretas = Integer.parseInt(equipas);
                } else if (count == 2) {
                    invalidasPretas = Integer.parseInt(equipas);
                }
//Repor a informação da equipa Branca
                else if (count == 3) {
                    validasBrancas = Integer.parseInt(equipas);
                } else if (count == 4) {
                    capturadasBrancas = Integer.parseInt(equipas);
                } else if (count == 5) {
                    invalidasBrancas = Integer.parseInt(equipas);
                }
                count++;
            }

//Repor a informação do jogo
            turno = turnoAntigo;
            semCaptura = capturasAntigas;
            capturaPrevia = antigaCapturaPrevia;

            countAnulaJogada++;
        }
    }

//Grava o jogo como ele se encontra atualmente
    public boolean gravarJogo(File ficheiroDestino){
        String newLine=System.getProperty("line.separator");
        String linhaAdicionar;
        try{
            FileWriter escrever = new FileWriter(ficheiroDestino);

            for(int secao=0;secao<=4;secao++){
                if(secao == 0){
                    escrever.write("" + tamanhoTabuleiro);
                    escrever.write(newLine);
                }

                if(secao == 1){
                    escrever.write("" + pecasMalucas.size());
                    escrever.write(newLine);
                }

                if(secao == 2){
                    for(CrazyPiece peace : pecasMalucas){
                        linhaAdicionar = "" + peace.getId() + ":" + peace.getTipoPeca() + ":" + peace.getEquipa() + ":" + peace.getAlcunha();
                        escrever.write(linhaAdicionar);
                        escrever.write(newLine);
                    }
                }

                if(secao == 3){
                    for(int y=0;y<tamanhoTabuleiro;y++){
                        linhaAdicionar="";
                        for(int x=0;x<tamanhoTabuleiro;x++){
                            CrazyPiece peace = receberPeca(x,y,pecasMalucas);
                            if(x < tamanhoTabuleiro-1){
                                if(peace != null) {
                                    linhaAdicionar = linhaAdicionar + peace.getId() + ":";
                                }else{
                                    linhaAdicionar = linhaAdicionar + "0:";
                                }
                            }
                            else if(x == tamanhoTabuleiro-1){
                                if(peace != null) {
                                    linhaAdicionar = linhaAdicionar + peace.getId();
                                }else{
                                    linhaAdicionar = linhaAdicionar + "0";
                                }
                            }

                        }
                        escrever.write(linhaAdicionar);
                        escrever.write(newLine);
                    }
                }
                else if(secao == 4){
                    linhaAdicionar = getIDEquipaAJogar() + ":" + validasPretas + ":" + capturadasPretas + ":" + invalidasPretas + ":" + validasBrancas + ":" + capturadasBrancas + ":" + invalidasBrancas;
                    escrever.write(linhaAdicionar);
                }
            }
            escrever.close();
        } catch (IOException e) {
            System.out.println("Deu erro ao guardar o jogo");
        }
        return true;
    }

//Reenicia as variaveis do jogo
    public void reset(){
        pecasMalucas.clear();
        turno=0;
        this.semCaptura=0;
        this.capturaPrevia=false;
        this.validasPretas = 0;
        this.capturadasPretas = 0;
        this.invalidasPretas = 0;
        this.validasBrancas = 0;
        this.capturadasBrancas = 0;
        this.invalidasBrancas = 0;
    }
}