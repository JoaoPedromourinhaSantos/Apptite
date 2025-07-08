// Indicadores.js

document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = 'http://localhost:8080';

    // Elementos para o Indicador de Engajamento
    const progressBarEngajamento = document.getElementById('progressBarEngajamento');
    const infoTextEngajamento = document.getElementById('infoTextEngajamento');

    // Elementos para o Indicador de Avaliações Negativas
    const progressBarAvaliacoesNegativas = document.getElementById('progressBarAvaliacoesNegativas');
    const infoTextAvaliacoesNegativas = document.getElementById('infoTextAvaliacoesNegativas');

    // Elementos para o Indicador de Taxa de Resposta
    const progressBarTaxaResposta = document.getElementById('progressBarTaxaResposta');
    const infoTextTaxaResposta = document.getElementById('infoTextTaxaResposta');

    // Novos elementos para o Indicador de Taxa de Comparecimento
    const progressBarTaxaComparecimento = document.getElementById('progressBarTaxaComparecimento');
    const infoTextTaxaComparecimento = document.getElementById('infoTextTaxaComparecimento');


    // Lógica para OBTER O ID DO RESTAURANTE DA URL (MANTIDA)
    const urlParams = new URLSearchParams(window.location.search);
    const idRestauranteParam = urlParams.get('idRestaurante');

    let ID_RESTAURANTE;

    if (idRestauranteParam) {
        ID_RESTAURANTE = parseInt(idRestauranteParam);
        if (isNaN(ID_RESTAURANTE)) {
            console.error("ID do restaurante na URL não é um número válido.");
            infoTextAvaliacoesNegativas.textContent = "Erro: ID de restaurante inválido na URL.";
            infoTextTaxaResposta.textContent = "Erro: ID de restaurante inválido na URL.";
            infoTextTaxaComparecimento.textContent = "Erro: ID de restaurante inválido na URL."; // Para o novo indicador
            return;
        }
    } else {
        console.error("ID do restaurante não encontrado na URL. Utilize ?idRestaurante=X");
        infoTextAvaliacoesNegativas.textContent = "Erro: ID do restaurante não fornecido.";
        infoTextTaxaResposta.textContent = "Erro: ID do restaurante não fornecido.";
        infoTextTaxaComparecimento.textContent = "Erro: ID do restaurante não fornecido."; // Para o novo indicador
        return;
    }

    console.log("ID do Restaurante obtido da URL:", ID_RESTAURANTE);


    /**
     * Função assíncrona para buscar os dados do indicador de engajamento (favoritos) da API.
     */
    async function fetchIndicadorEngajamento() {
        if (!progressBarEngajamento || !infoTextEngajamento) {
            console.error("Elementos da UI para engajamento não encontrados.");
            return;
        }

        infoTextEngajamento.textContent = "Calculando...";
        progressBarEngajamento.style.width = '0%';
        progressBarEngajamento.textContent = '0%';

        try {
            const response = await fetch(`${API_BASE_URL}/favoritos/indicador/percentual-engajamento`);
            if (!response.ok) {
                throw new Error(`Erro ao buscar dados do indicador de engajamento: ${response.status}`);
            }

            const data = await response.json();
            const percentual = data.valorPercentual || 0;

            progressBarEngajamento.style.width = percentual.toFixed(2) + '%';
            progressBarEngajamento.textContent = percentual.toFixed(2) + '%';
            infoTextEngajamento.textContent = `Clientes favoritaram ${percentual.toFixed(2)}% dos restaurantes cadastrados.`;

        } catch (error) {
            console.error("Falha ao carregar o indicador de engajamento:", error);
            infoTextEngajamento.textContent = "Não foi possível calcular o indicador de engajamento.";
            progressBarEngajamento.style.backgroundColor = '#dc3545';
            progressBarEngajamento.textContent = 'Erro';
        }
    }

    /**
     * Função assíncrona para buscar o percentual de avaliações negativas de um restaurante.
     */
    async function fetchPercentualAvaliacoesNegativas(idRestaurante) {
        if (!progressBarAvaliacoesNegativas || !infoTextAvaliacoesNegativas) {
            console.error("Elementos da UI para avaliações negativas não encontrados.");
            return;
        }

        infoTextAvaliacoesNegativas.textContent = "Calculando...";
        progressBarAvaliacoesNegativas.style.width = '0%';
        progressBarAvaliacoesNegativas.textContent = '0%';

        try {
            const response = await fetch(`${API_BASE_URL}/avaliacoes/indicadores/percentual-avaliacoes-negativas/${idRestaurante}`);

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error(`Restaurante com ID ${idRestaurante} não encontrado.`);
                }
                throw new Error(`Erro ao buscar percentual de avaliações negativas: ${response.status}`);
            }

            const data = await response.json();
            const percentual = data.percentualAvaliacoesNegativas || 0;

            progressBarAvaliacoesNegativas.style.width = percentual.toFixed(2) + '%';
            progressBarAvaliacoesNegativas.textContent = percentual.toFixed(2) + '%';
            infoTextAvaliacoesNegativas.textContent = `Das avaliações do restaurante, ${percentual.toFixed(2)}% são negativas (nota < 3).`;

            // Adaptação da cor da barra para avaliações negativas (opcional)
            if (percentual > 50) {
                progressBarAvaliacoesNegativas.style.background = 'linear-gradient(90deg, #dc3545, #8b0000)';
            } else if (percentual > 20) {
                progressBarAvaliacoesNegativas.style.background = 'linear-gradient(90deg, #ffc107, #fd7e14)';
            } else {
                progressBarAvaliacoesNegativas.style.background = 'linear-gradient(90deg, #28a745, #218838)';
            }

        } catch (error) {
            console.error("Falha ao carregar o percentual de avaliações negativas:", error);
            infoTextAvaliacoesNegativas.textContent = `Não foi possível calcular o indicador de avaliações negativas: ${error.message}`;
            progressBarAvaliacoesNegativas.style.backgroundColor = '#dc3545';
            progressBarAvaliacoesNegativas.textContent = 'Erro';
        }
    }

    /**
     * Função assíncrona para buscar a taxa de resposta às avaliações de um restaurante.
     */
    async function fetchTaxaDeResposta(idRestaurante) {
        if (!progressBarTaxaResposta || !infoTextTaxaResposta) {
            console.error("Elementos da UI para taxa de resposta não encontrados.");
            return;
        }

        infoTextTaxaResposta.textContent = "Calculando...";
        progressBarTaxaResposta.style.width = '0%';
        progressBarTaxaResposta.textContent = '0%';

        try {
            const response = await fetch(`${API_BASE_URL}/avaliacoes/indicadores/taxa-resposta/${idRestaurante}`);

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error(`Restaurante com ID ${idRestaurante} não encontrado para calcular taxa de resposta.`);
                }
                throw new Error(`Erro ao buscar taxa de resposta: ${response.status}`);
            }

            const data = await response.json();
            const taxa = data.taxaDeResposta || 0;

            progressBarTaxaResposta.style.width = taxa.toFixed(2) + '%';
            progressBarTaxaResposta.textContent = taxa.toFixed(2) + '%';
            infoTextTaxaResposta.textContent = `O restaurante respondeu a ${taxa.toFixed(2)}% das avaliações recebidas.`;

            // Adaptação da cor da barra para taxa de resposta (opcional)
            if (taxa < 50) {
                progressBarTaxaResposta.style.background = 'linear-gradient(90deg, #dc3545, #8b0000)';
            } else if (taxa < 80) {
                progressBarTaxaResposta.style.background = 'linear-gradient(90deg, #ffc107, #fd7e14)';
            } else {
                progressBarTaxaResposta.style.background = 'linear-gradient(90deg, #28a745, #218838)';
            }

        } catch (error) {
            console.error("Falha ao carregar a taxa de resposta:", error);
            infoTextTaxaResposta.textContent = `Não foi possível calcular a taxa de resposta: ${error.message}`;
            progressBarTaxaResposta.style.backgroundColor = '#dc3545';
            progressBarTaxaResposta.textContent = 'Erro';
        }
    }

    /**
     * NOVA FUNÇÃO assíncrona para buscar a taxa de comparecimento às reservas de um restaurante.
     */
    async function fetchTaxaComparecimento(idRestaurante) {
        if (!progressBarTaxaComparecimento || !infoTextTaxaComparecimento) {
            console.error("Elementos da UI para taxa de comparecimento não encontrados.");
            return;
        }

        infoTextTaxaComparecimento.textContent = "Calculando...";
        progressBarTaxaComparecimento.style.width = '0%';
        progressBarTaxaComparecimento.textContent = '0%';

        try {
            const response = await fetch(`${API_BASE_URL}/reservas/indicadores/taxa-comparecimento/${idRestaurante}`);

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error(`Restaurante com ID ${idRestaurante} não encontrado para calcular taxa de comparecimento.`);
                }
                throw new Error(`Erro ao buscar taxa de comparecimento: ${response.status}`);
            }

            const data = await response.json();
            const taxa = data.taxa || 0; // O JSON de retorno do backend é { "taxa": 85.5 }

            progressBarTaxaComparecimento.style.width = taxa.toFixed(2) + '%';
            progressBarTaxaComparecimento.textContent = taxa.toFixed(2) + '%';
            infoTextTaxaComparecimento.textContent = `Clientes compareceram em ${taxa.toFixed(2)}% das reservas concluídas.`;

            // Adaptação da cor da barra para taxa de comparecimento (opcional)
            if (taxa < 50) {
                progressBarTaxaComparecimento.style.background = 'linear-gradient(90deg, #dc3545, #8b0000)'; // Vermelho para baixa taxa
            } else if (taxa < 80) {
                progressBarTaxaComparecimento.style.background = 'linear-gradient(90deg, #ffc107, #fd7e14)'; // Laranja para média taxa
            } else {
                progressBarTaxaComparecimento.style.background = 'linear-gradient(90deg, #28a745, #218838)'; // Verde para alta taxa
            }

        } catch (error) {
            console.error("Falha ao carregar a taxa de comparecimento:", error);
            infoTextTaxaComparecimento.textContent = `Não foi possível calcular a taxa de comparecimento: ${error.message}`;
            progressBarTaxaComparecimento.style.backgroundColor = '#dc3545';
            progressBarTaxaComparecimento.textContent = 'Erro';
        }
    }


    // Chama todas as funções para buscar os dados quando a página carregar
    fetchIndicadorEngajamento();
    fetchPercentualAvaliacoesNegativas(ID_RESTAURANTE);
    fetchTaxaDeResposta(ID_RESTAURANTE);
    fetchTaxaComparecimento(ID_RESTAURANTE); // Chamada para o novo indicador
});