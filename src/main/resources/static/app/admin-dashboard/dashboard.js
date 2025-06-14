import { checkAuth } from '/utils/auth-helper.js';

checkAuth('ADMIN');

document.getElementById('logout-btn').addEventListener('click', () => {
  localStorage.removeItem('token');
  window.location.href = '/app/login/index.html';
});
