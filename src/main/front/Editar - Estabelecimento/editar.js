// Arquivo para editar o perfil do restaurante

document.addEventListener("DOMContentLoaded", function () {
    const API_BASE_URL = 'http://localhost:8080';

    // --- Elementos do DOM ---
    const fileInputGaleria = document.getElementById("galeria");
    const previaContainerGaleria = document.getElementById('file-list');
    const confirmarBtn = document.getElementById("confirmarBtn");

    // --- Constantes e Variáveis de Estado ---
    const MAX_FILES = 10;
    let arquivosGaleriaParaEnviar = [];
    let restauranteLogado = null;
    let idRestaurante = null;

    // =================================================================================
    // FUNÇÕES DE AJUDA
    // =================================================================================

    function renderizarPreviasGaleria() {
        previaContainerGaleria.innerHTML = '';
        if (arquivosGaleriaParaEnviar.length === 0) {
            previaContainerGaleria.innerHTML = '<p>Nenhum arquivo escolhido.</p>';
            return;
        }

        arquivosGaleriaParaEnviar.forEach((item, index) => {
            const isNewFile = item instanceof File;
            const fileName = isNewFile ? item.name : (item.nomeOriginalImagem || `imagem_existente_${item.id}.jpg`);
            const imageSrc = isNewFile ? URL.createObjectURL(item) : `${API_BASE_URL}/restaurante/galeria/imagem/${item.id}`;
            const nomeExibicao = fileName.length > 25 ? fileName.substring(0, 22) + "..." : fileName;

            const itemContainer = document.createElement('div');
            itemContainer.className = 'galeria-item-representacao';
            if (!isNewFile) {
                itemContainer.dataset.imageId = item.id;
            }

            itemContainer.innerHTML = `
                <span class="galeria-nome-arquivo">${nomeExibicao}</span>
                <div class="galeria-item-botoes">
                    <button type="button" class="btn-visualizar-previa">Visualizar</button>
                    <button type="button" class="btn-remover-previa">Remover</button>
                </div>
            `;

            itemContainer.querySelector('.btn-visualizar-previa').onclick = () => abrirModalComImagem(imageSrc, fileName);
            itemContainer.querySelector('.btn-remover-previa').onclick = () => removerImagemDaPrevia(index, itemContainer, isNewFile);
            
            previaContainerGaleria.appendChild(itemContainer);
            if (isNewFile) {
                itemContainer.dataset.objectUrl = imageSrc;
            }
        });
    }

    function removerImagemDaPrevia(indexParaRemover, itemContainer, isNewFile) {
        if (isNewFile) {
            const objectUrl = itemContainer.dataset.objectUrl;
            if (objectUrl) URL.revokeObjectURL(objectUrl);
        }
        arquivosGaleriaParaEnviar.splice(indexParaRemover, 1);
        renderizarPreviasGaleria();
    }

    function abrirModalComImagem(imageSrc, nomeArquivo) {
        const modal = document.getElementById('modalVisualizacaoImagem');
        const imgElement = document.getElementById('imagemEmModal');
        const tituloModal = document.getElementById('modalImagemTitulo');

        if (modal && imgElement && tituloModal) {
            imgElement.src = imageSrc;
            tituloModal.textContent = "Visualizando: " + nomeArquivo;
            modal.style.display = 'flex';
        }
    }

    // Associar a função de fechar ao botão do modal
    window.fecharModalImagem = function() {
        const modal = document.getElementById('modalVisualizacaoImagem');
        if (modal) {
            modal.style.display = 'none';
            document.getElementById('imagemEmModal').src = "";
        }
    }

    // --- LÓGICA PRINCIPAL ---

    function carregarDadosIniciais() {
        // Carrega os dados do restaurante e preenche o formulário
        restauranteLogado = JSON.parse(localStorage.getItem("restauranteLogado"));
        if (!restauranteLogado || !restauranteLogado.idRestaurante) {
            alert("Restaurante não encontrado. Faça login novamente.");
            window.location.href = "../Login/login.html";
            return;
        }
        idRestaurante = restauranteLogado.idRestaurante;

        // Preenche os campos do formulário com os dados atuais
        document.getElementById("name").value = restauranteLogado.nomeRestaurante || "";
        document.getElementById("email").value = restauranteLogado.emailRestaurante || "";
        document.getElementById("telefone").value = restauranteLogado.telefoneRestaurante || "";
        document.getElementById("description").value = restauranteLogado.descricaoRestaurante || "";
        document.getElementById("faixaP").value = restauranteLogado.faixaPreco || "";
        document.getElementById("cnpj").value = restauranteLogado.cnpj || "";
        document.getElementById("rua").value = restauranteLogado.ruaEndereco || "";
        document.getElementById("bairro").value = restauranteLogado.bairroEndereco || "";
        document.getElementById("numero").value = restauranteLogado.numeroEndereco || "";
        document.getElementById("cidade").value = restauranteLogado.cidadeEndereco || "";
        document.getElementById("estado").value = restauranteLogado.estadoEndereco || "";

        // Preenche a galeria com imagens existentes
        if (restauranteLogado.imagensGaleria) {
            arquivosGaleriaParaEnviar = restauranteLogado.imagensGaleria.map(img => ({
                id: img.id,
                nomeOriginalImagem: img.nomeOriginalImagem,
                ordem: img.ordem
            }));
        }
        renderizarPreviasGaleria();
    }

    function adicionarListeners() {
        // Listener para o input de novos arquivos da galeria
        if (fileInputGaleria) {
            fileInputGaleria.addEventListener('change', function(event) {
                const novosArquivos = Array.from(event.target.files);
                for (const novoArquivo of novosArquivos) {
                    // Adicionar validações de tipo e tamanho aqui, se desejar
                    if (arquivosGaleriaParaEnviar.length < MAX_FILES) {
                        arquivosGaleriaParaEnviar.push(novoArquivo);
                    } else {
                        alert(`Você pode selecionar no máximo ${MAX_FILES} imagens.`);
                        break;
                    }
                }
                renderizarPreviasGaleria();
                fileInputGaleria.value = '';
            });
        }

        // Listener para o botão de confirmar/salvar
        confirmarBtn.addEventListener("click", async function (e) {
            e.preventDefault();
            
            const formData = new FormData();
            
            // Monta o FormData com os nomes de campo corretos para o DTO
            formData.append("nomeRestaurante", document.getElementById("name").value);
            formData.append("emailRestaurante", document.getElementById("email").value);
            formData.append("telefoneRestaurante", document.getElementById("telefone").value);
            formData.append("descricaoRestaurante", document.getElementById("description").value);
            formData.append("faixaPreco", document.getElementById("faixaP").value);
            formData.append("cnpj", document.getElementById("cnpj").value);
            formData.append("ruaEndereco", document.getElementById("rua").value);
            formData.append("bairroEndereco", document.getElementById("bairro").value);
            formData.append("numeroEndereco", document.getElementById("numero").value);
            formData.append("cidadeEndereco", document.getElementById("cidade").value);
            formData.append("estadoEndereco", document.getElementById("estado").value);
            
            
            const senha = document.getElementById("password").value;
            if (senha) {
                formData.append("senhaRestaurante", senha);
                formData.append("confirmarSenhaRestaurante", document.getElementById("confirm-password").value);
            }

            const fileInputCardapio = document.getElementById("cardapio");
            if (fileInputCardapio.files.length > 0) {
                formData.append("file", fileInputCardapio.files[0]);
            }

            // Separa arquivos novos dos IDs de imagens existentes
            const filesToUpload = [];
            const existingImageIdsToRetain = [];
            arquivosGaleriaParaEnviar.forEach(item => {
                if (item instanceof File) {
                    filesToUpload.push(item);
                } else if (item.id) {
                    existingImageIdsToRetain.push(item.id);
                }
            });
            filesToUpload.forEach(file => formData.append("galeria", file));
            existingImageIdsToRetain.forEach(id => formData.append("imagensGaleriaParaManter", id));

            try {
                // Chama o endpoint correto com o método PUT
                const response = await fetch(`${API_BASE_URL}/restaurante/editar/${idRestaurante}`, {
                    method: "PUT",
                    body: formData
                });

                if (!response.ok) throw new Error(await response.text());
                
                const dataAtualizada = await response.json();
                
                // Atualiza o localStorage com os dados retornados pela API
                localStorage.setItem("restauranteLogado", JSON.stringify(dataAtualizada));
                
                alert("Informações salvas com sucesso!");
                window.location.href = "../Perfil - Estabelecimento/perfil_estabelecimento.html";
            } catch (error) {
                console.error("Erro ao atualizar perfil:", error);
                alert("Erro ao atualizar perfil: " + error.message);
            }
        });
        
        /// Modal de exclusão de conta
    const modal = document.getElementById("myModal");
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.getElementById("closeModal");
    const deleteAccountBtn = document.getElementById("deleteAccount");

    modal.style.display = "none";

    openModalBtn.addEventListener("click", function () {
        modal.style.display = "block";
    });

    closeModalBtn.addEventListener("click", function () {
        modal.style.display = "none";
    });

    deleteAccountBtn.addEventListener("click", function () {
        const idRestaurante = restauranteLogado.idRestaurante;
        if (!idRestaurante) {
            alert("Erro: Não foi possível identificar o restaurante.");
            return;
        }

        fetch(`http://localhost:8080/restaurante/deletar/${idRestaurante}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            }
        })
            .then(response => {
                if (response.ok) {
                    localStorage.removeItem("restauranteLogado");
                    alert("Conta excluída com sucesso!");
                    window.location.href = "../Login/login.html";
                } else {
                    alert("Erro ao excluir a conta.");
                }
            })
            .catch(error => {
                console.error("Erro ao excluir conta:", error);
                alert("Erro ao tentar excluir a conta.");
            });
    });

    window.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
    }

    // --- INICIALIZAÇÃO ---
    carregarDadosIniciais();
    adicionarListeners();
});