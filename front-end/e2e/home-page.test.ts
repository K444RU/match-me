import { expect, test } from '@playwright/test';

test.describe('navigation', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('has Blind in navbar', async ({ page }) => {
    await expect(page).toHaveTitle(/Blind/);
  });

  test('log in link navigates to login page', async ({ page }) => {
    await page.getByRole('link', { name: 'Log in' }).click();
    await expect(page.getByRole('heading', { name: 'Log in' })).toBeVisible();
  });

  test('sign up link navigates to signup page', async ({ page }) => {
    await page.getByRole('link', { name: 'Sign up' }).click();
    await expect(page.getByRole('heading', { name: 'Sign up' })).toBeVisible();
  });
});

test.describe('main content', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('hero section is visible', async ({ page }) => {
    await expect(page.getByRole('heading', { level: 1 })).toBeVisible();
  });

  test('how it works section is visible', async ({ page }) => {
    await expect(page.getByTestId('how-it-works-section')).toBeVisible();
  });

  test('learn more button scrolls to how it works section', async ({ page }) => {
    const learnMoreButton = page.getByRole('button', { name: /learn more/i });
    await learnMoreButton.click();

    await expect(page.getByTestId('how-it-works-section')).toBeInViewport();
  });
});
