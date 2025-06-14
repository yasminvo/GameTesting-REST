// auth-helper.js

// Função para obter o token do localStorage
function getToken() {
  return localStorage.getItem('token');
}

// Função para decodificar um JWT e retornar o payload
function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = decodeURIComponent(atob(base64Url).split('').map(c =>
      '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    ).join(''));
    return JSON.parse(base64);
  } catch (e) {
    return null;
  }
}

export function checkAuth(requiredRole = null) {
  const token = getToken();

  if (!token) {
    redirectToLogin();
    return;
  }

  const payload = parseJwt(token);

  if (!payload || !payload.sub || (requiredRole && payload.role !== requiredRole)) {
    redirectToLogin();
    return;
  }

  // Token válido, você pode também exibir o nome do usuário, por exemplo
  console.log("Usuário autenticado:", payload.sub, "| Papel:", payload.role);
}

// Redireciona para a página de login
function redirectToLogin() {
  window.location.href = '/public/login/index.html';
}
