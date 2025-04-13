import { expect, test } from '@playwright/test';

test.describe('User Login', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000/');
  });

  test('should login successfully and redirect to chats', async ({ page }) => {
    await page.getByRole('link', { name: 'Log in' }).click();
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('john.doe@example.com');
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('123456');
    await page.getByRole('button', { name: 'Submit form.' }).click();

    await expect(page).toHaveURL('http://localhost:3000/chats');
  });

  test('should login successfully and redirect back on logout', async ({ page }) => {
    await page.getByRole('link', { name: 'Log in' }).click();
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('john.doe@example.com');
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('123456');
    await page.getByRole('button', { name: 'Submit form.' }).click();
    await expect(page).toHaveURL('http://localhost:3000/chats');

    await page.getByRole('button', { name: 'John John Doe johnny' }).click();
    await page.getByRole('menuitem', { name: 'Log out' }).click();

    await expect(page).toHaveURL('http://localhost:3000/login');
  });

  test('should display login form', async ({ page }) => {
    await page.getByRole('link', { name: 'Log in' }).click();

    await expect(page.getByRole('heading', { name: 'Log in' })).toBeVisible();
    await expect(page.getByRole('heading')).toContainText('Log in');

    await expect(page.getByRole('textbox', { name: 'Email' })).toBeVisible();
    await expect(page.getByRole('textbox', { name: 'Password' })).toBeVisible();

    await expect(page.getByRole('button', { name: 'Submit form.' })).toBeVisible();
    await expect(page.getByLabel('Submit form.').locator('span')).toContainText('Login');
  });

  test('should show error for invalid credentials', async ({ page }) => {
    await page.getByRole('link', { name: 'Log in' }).click();
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('invalidUser@example.com');
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('wrongpassword');
    await page.getByRole('button', { name: 'Submit form.' }).click();

    await expect(page.getByText('Invalid CredentialsPlease')).toBeVisible();
    await expect(page.locator('h2')).toContainText('Invalid Credentials');
    await expect(page.getByRole('paragraph')).toContainText('Please check your email and password');
  });
});
