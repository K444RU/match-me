import { expect, test } from '@playwright/test';
import { getRandomValues } from 'utils/utils';

test.describe('User Registration', () => {
  const testUser = getRandomValues();

  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000/');
  });

  test('should successfully register', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign Up' }).click();
    await expect(page.getByRole('heading', { name: 'Sign Up' })).toBeVisible();

    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill(testUser.email);
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill(testUser.password);
    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill(testUser.phone);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    // Has success message
    await expect(page.locator('h2')).toContainText('Nice! You have been registered.');
    await expect(page.getByRole('paragraph')).toContainText('Please verify your email.');

    // Cleared the fields
    await expect(page.getByRole('textbox', { name: 'Email' })).toBeEmpty();
    await expect(page.getByRole('textbox', { name: 'Password' })).toBeEmpty();

    // Did not redirect to login page
    await expect(page).toHaveURL('http://localhost:3000/register');
  });

  test('should display registration form', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign Up' }).click();
    await expect(page.getByRole('heading', { name: 'Sign Up' })).toBeVisible();
    await expect(page.getByRole('heading')).toContainText('Sign Up');
    await expect(page.getByRole('textbox', { name: 'Email' })).toBeVisible();
    await expect(page.getByRole('textbox', { name: 'Password' })).toBeVisible();
    await expect(page.getByRole('textbox', { name: 'Enter a phone number' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Submit form.' })).toBeVisible();
    await expect(page.getByLabel('Submit form.').locator('span')).toContainText('Register');
  });

  test('should show error for invalid phone number', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign Up' }).click();

    // Fill in correct fields
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill(testUser.email);
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill(testUser.password);

    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill('+372 11111111');
    await page.getByRole('button', { name: 'Submit form.' }).click();

    // Has error message
    await expect(page.locator('[id="«r2»-form-item-message"]')).toContainText('Invalid phone number');

    // Did not redirect to login page
    await expect(page).toHaveURL('http://localhost:3000/register');
  });

  test('should show error for already registered email', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign Up' }).click();
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('john.doe@example.com');
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('123456');
    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill(testUser.phone);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    // Has error message
    await expect(page.locator('h2')).toContainText('Registration Failed');
    await expect(page.getByRole('paragraph')).toContainText('Email already exists');

    // Did not redirect to login page
    await expect(page).toHaveURL('http://localhost:3000/register');
  });
});
