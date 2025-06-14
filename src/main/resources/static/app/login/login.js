const form = document.getElementById('login-form');
const guestBtn = document.getElementById('guest-btn');
const errorMsg = document.getElementById('error-message');

console.log("aaa", document.getElementById('login-form'));

form.addEventListener('submit', async (e) => {
  e.preventDefault();

  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;



  try {
    const response = await fetch('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);

      const payload = JSON.parse(atob(data.token.split('.')[1]));
      console.log('role js', payload)
      const role = payload.role;

      console.log('Payload:', payload);
      console.log('Role:', role);
      console.log('Type of Role:', typeof role);
      debugger;


      // Redirecionamento baseado no papel
      if (role === "ADMIN") {
        window.location.href = '/app/admin-dashboard/index.html';
      } else {
        window.location.href = '/app/tester-dashboard/index.html'; // ajuste se existir
      }

    } else {
      errorMsg.textContent = 'Email ou senha inválidos.';
    }
  } catch (err) {
    console.error('Erro ao fazer login:', err);
    errorMsg.textContent = 'Erro inesperado. Tente novamente.';
  }
});

guestBtn.addEventListener('click', () => {
  window.location.href = '/app/dashboard-visitante/index.html'; // ajuste se existir
});
