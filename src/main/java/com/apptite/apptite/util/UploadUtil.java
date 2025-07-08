package com.apptite.apptite.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.UUID;

// Método para fazer o upload da imagem e retornar o caminho
// Método para fazer o upload da imagem e retornar o caminho

public class UploadUtil {

    private static final String CAMINHO_ABSOLUTO = "C:/Users/arthu/OneDrive/Área de Trabalho/PastaBackUp/pmg-es-2025-1-ti2-3687100-restaurante/src/main/resources/static/";

    public static String fazerUploadImagem(MultipartFile imagem) {
        String nomeOriginal = imagem.getOriginalFilename();
        String nomeUnico = UUID.randomUUID() + "_" + nomeOriginal;
        
        // Caminho relativo a partir da pasta static
        String caminhoRelativo = "images/img-uploads/" + nomeUnico;
        String caminhoDestinoAbsoluto = CAMINHO_ABSOLUTO + caminhoRelativo;


        try {
            File destino = new File(caminhoDestinoAbsoluto);

            // Cria os diretórios se não existirem
            destino.getParentFile().mkdirs();

            // Salva o arquivo no caminho absoluto
            imagem.transferTo(destino);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao fazer upload da imagem", e);
        }

        // Retorna apenas o caminho relativo para salvar no banco
        return caminhoRelativo;
    }

    
}
