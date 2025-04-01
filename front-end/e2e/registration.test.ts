import { expect, test } from '@playwright/test';
import { getRandomValues } from 'utils/utils';

test.describe('User Registration', () => {
  const testUser = getRandomValues();

  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000/');
  });

  test('should successfully register and redirect to login page', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign Up' }).click();
    await expect(page.getByRole('heading', { name: 'Sign Up' })).toBeVisible();

    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill(testUser.email);
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill(testUser.password);
    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill(testUser.phone);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    await expect(page).toHaveURL('http://localhost:3000/login');
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
    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill('12345678');

    await expect(page.getByText('Invalid phone number')).toBeVisible();
    await expect(page.getByRole('paragraph')).toContainText('Invalid phone number');
  });

  test('should show error for already registered email', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign Up' }).click();
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('test1@example.com');
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('123456');
    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill(testUser.phone);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    await expect(page.getByText('We found some errors Email')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'We found some errors' })).toBeVisible();
    await expect(page.locator('h2')).toContainText('We found some errors');
    await expect(page.getByText('Email already exists')).toBeVisible();
    await expect(page.getByRole('paragraph')).toContainText('Email already exists');
  });
});
