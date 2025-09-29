-- Create database if not exists
CREATE DATABASE IF NOT EXISTS small_business;

USE small_business;

-- Create ENUM equivalent for payment status using ENUM type
CREATE TABLE payment_status_enum (
    status VARCHAR(20) PRIMARY KEY
);

-- Insert possible status values
INSERT INTO payment_status_enum (status) VALUES 
    ('PENDING'), ('PAID'), ('OVERDUE'), ('PARTIALLY_PAID'), ('CANCELLED'), ('REFUNDED');

-- Create ENUM equivalent for payment method using ENUM type
CREATE TABLE payment_method_enum (
    method VARCHAR(20) PRIMARY KEY
);

-- Insert possible method values
INSERT INTO payment_method_enum (method) VALUES 
    ('CREDIT_CARD'), ('BANK_TRANSFER'), ('CASH'), ('CHECK'), ('OTHER');

-- Create invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(255) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    amount_paid DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_status FOREIGN KEY (status) REFERENCES payment_status_enum(status),
    CONSTRAINT chk_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_amount_paid_positive CHECK (amount_paid >= 0),
    CONSTRAINT chk_dates_valid CHECK (due_date >= issue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_method FOREIGN KEY (payment_method) REFERENCES payment_method_enum(method),
    CONSTRAINT chk_payment_amount_positive CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_invoices_customer_email ON invoices(customer_email);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
