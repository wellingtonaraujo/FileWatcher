package com.wellington.filewatcher;

public class ExameTipo {
    private String exame;   // Nome completo do exame
    private String sufixo;  // Abreviação (ex: PTC)

    public ExameTipo(String exame, String sufixo) {
        this.exame = exame;
        this.sufixo = sufixo;
    }

    public String getExame() {
        return exame;
    }

    public void setExame(String exame) {
        this.exame = exame;
    }

    public String getSufixo() {
        return sufixo;
    }

    public void setSufixo(String sufixo) {
        this.sufixo = sufixo;
    }

    @Override
    public String toString() {
        // Exibição amigável na JList
        return sufixo + " - " + exame;
    }
}
