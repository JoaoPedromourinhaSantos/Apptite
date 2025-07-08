package com.apptite.apptite.dtos.RestauranteDTOs;

import com.apptite.apptite.model.Restaurante;
import java.util.List;
import java.util.stream.Collectors;
import com.apptite.apptite.dtos.FiltroSimplesDTO;
import java.util.Set;

// DTO auxiliar para informações da imagem da galeria
record ImagemGaleriaInfoDTO(Integer id, String nomeOriginalImagem, String tipoMimeImagem, int ordem) {
    public static ImagemGaleriaInfoDTO fromEntity(com.apptite.apptite.model.GaleriaRestaurante img) {
        if (img == null) return null;
        // Assumindo que ImagemGaleriaEstabelecimento tem getId(), getNomeOriginalImagem(), getTipoMimeImagem(), getOrdem()
        return new ImagemGaleriaInfoDTO(img.getId(), img.getNomeOriginalImagem(), img.getTipoMimeImagem(), img.getOrdem());
    }
}

public record RestauranteResponseDTO(
    Integer idRestaurante,
    String nomeRestaurante,
    String cnpj,
    String emailRestaurante,
    String telefoneRestaurante,
    String descricaoRestaurante,
    String faixaPreco,
    boolean temFotoCardapio, // URL ou identificador
    float avaliacaoGeral,
    String ruaEndereco,
    String bairroEndereco,
    String numeroEndereco,
    String cidadeEndereco,
    String estadoEndereco,
    List<ImagemGaleriaInfoDTO> imagensGaleria, // Lista de informações das imagens
    Set<FiltroSimplesDTO> filtros
) {
    public static RestauranteResponseDTO fromEntity(Restaurante restaurante) {
        if (restaurante == null) return null;
        List<ImagemGaleriaInfoDTO> galeriaDTOs = restaurante.getImagensGaleria() != null ?
            restaurante.getImagensGaleria().stream()
                       .map(ImagemGaleriaInfoDTO::fromEntity)
                       .collect(Collectors.toList()) :
            java.util.Collections.emptyList();

        Set<FiltroSimplesDTO> filtrosDTO = restaurante.getFiltros() != null ?
            restaurante.getFiltros().stream()
                       .map(FiltroSimplesDTO::fromEntity)
                       .collect(Collectors.toSet()) :
            java.util.Collections.emptySet();    

        return new RestauranteResponseDTO(
            restaurante.getIdRestaurante(),
            restaurante.getNomeRestaurante(),
            restaurante.getCnpj(),
            restaurante.getEmailRestaurante(),
            restaurante.getTelefoneRestaurante(),
            restaurante.getDescricaoRestaurante(),
            restaurante.getFaixaPreco(),
            restaurante.getFotoCardapio() != null && restaurante.getFotoCardapio().length > 0,
            restaurante.getAvaliacaoGeral(),
            restaurante.getRuaEndereco(),
            restaurante.getBairroEndereco(),
            restaurante.getNumeroEndereco(),
            restaurante.getCidadeEndereco(),
            restaurante.getEstadoEndereco(),
            galeriaDTOs,
            filtrosDTO
        );
    }
}