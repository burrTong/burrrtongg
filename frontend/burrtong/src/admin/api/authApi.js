const API_BASE_URL = '';

export const registerAdmin = async (registrationData) => {
  const { name, email, password } = registrationData;
  const response = await fetch(`${API_BASE_URL}/api/admin/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name, email, password }),
  });

  const responseBodyText = await response.text();

  if (!response.ok) {
    console.error("Admin Register API Error Status:", response.status);
    console.error("Admin Register API Error Body:", responseBodyText);
    let errorMessage = 'Admin registration failed';
    try {
      const errorData = JSON.parse(responseBodyText);
      errorMessage = errorData.message || errorMessage;
    } catch (e) {
      errorMessage = responseBodyText;
    }
    throw new Error(errorMessage);
  }

  try {
    return JSON.parse(responseBodyText);
  } catch (e) {
    console.error("Admin Register API Success, but response was not valid JSON:", responseBodyText);
    throw new Error('Admin registration successful, but response was not valid JSON.');
  }
};

export const login = async (email, password) => {
  const response = await fetch(`${API_BASE_URL}/api/auth/admin/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  });

  const responseBodyText = await response.text();

  if (!response.ok) {
    console.error("Admin Login API Error Status:", response.status);
    console.error("Admin Login API Error Body:", responseBodyText);
    let errorMessage = 'Admin login failed';
    try {
      const errorData = JSON.parse(responseBodyText);
      errorMessage = errorData.message || errorMessage;
    } catch (e) {
      errorMessage = responseBodyText;
    }
    throw new Error(errorMessage);
  }

  try {
    return JSON.parse(responseBodyText);
  } catch (e) {
    console.error("Admin Login API Success, but response was not valid JSON:", responseBodyText);
    throw new Error('Admin login successful, but response was not valid JSON.');
  }
};