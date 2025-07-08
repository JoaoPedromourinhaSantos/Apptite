document.addEventListener("DOMContentLoaded", () => {
    // Seleciona o link do perfil pelo ID que acabamos de adicionar
    const linkPerfil = document.getElementById("link-perfil");
    if (!linkPerfil) return; // Se não encontrar o link, não faz nada

    // Tenta pegar os dados de login do localStorage
    const usuarioLogadoStorage = localStorage.getItem("usuarioLogado");
    const restauranteLogadoStorage = localStorage.getItem("restauranteLogado");

    if (usuarioLogadoStorage) {
        // Se um CLIENTE está logado...
        const usuario = JSON.parse(usuarioLogadoStorage);
        // Atualiza o link para apontar para o perfil do cliente
        linkPerfil.href = "../Perfil - Usuário/perfil_usuario.html";
        // Opcional: Adicionar um 'title' para melhor experiência
        linkPerfil.title = `Ver perfil de ${usuario.nomeCliente || 'usuário'}`;

    } else if (restauranteLogadoStorage) {
        // Se um RESTAURANTE está logado...
        const restaurante = JSON.parse(restauranteLogadoStorage);
        // Atualiza o link para apontar para o perfil do restaurante logado
        // Nota: A página de perfil do próprio restaurante não precisa de ID na URL,
        // pois ela também lerá os dados do localStorage.
        linkPerfil.href = "../Perfil - Estabelecimento/perfil_estabelecimento.html";
        // Opcional: Adicionar um 'title'
        linkPerfil.title = `Ver painel de ${restaurante.nomeRestaurante || 'restaurante'}`;

    } else {
        // Se NINGUÉM está logado...
        // O link já aponta para a página de login por padrão no HTML,
        // mas podemos garantir aqui também.
        linkPerfil.href = "../Login/login.html";
        linkPerfil.title = "Fazer Login ou Cadastrar";
    }
});