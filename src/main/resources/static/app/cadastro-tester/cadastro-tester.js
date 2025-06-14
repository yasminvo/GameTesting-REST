import { checkAuth } from '/utils/auth-helper.js';

checkAuth('ADMIN');

const form = document.getElementById('admin-form');
const message = document.getElementById('message');

form.addEventListener('submit', async (e) => {
  e.preventDefault();

  const token = localStorage.getItem('token');
  if (!token) {
    window.location.href = '/app/login/index.html';
    return;
  }

  const name = document.getElementById('name').value.trim();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;

  try {
    const response = await fetch('/api/users/testers', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ name, email, password })
    });

    if (response.ok) {
      message.style.color = 'green';
      message.textContent = 'Administrador cadastrado com sucesso!';
      form.reset();
    } else {
      let errMsg = 'Erro ao cadastrar administrador.';
      try {
        const err = await response.json();
        errMsg = err.message || errMsg;
      } catch (e) {
        console.warn('Resposta sem JSON:', e);
      }
      message.style.color = 'red';
      message.textContent = errMsg;
    }

  } catch (err) {
    message.style.color = 'red';
    message.textContent = 'Erro inesperado. Tente novamente.';
    console.error(err);
  }
});
