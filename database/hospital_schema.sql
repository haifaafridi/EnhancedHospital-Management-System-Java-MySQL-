-- ============================================
-- CORRECTED HOSPITAL MANAGEMENT SYSTEM
-- DATABASE SCHEMA
-- ============================================

DROP DATABASE IF EXISTS hospital_management_system;
CREATE DATABASE hospital_management_system;
USE hospital_management_system;

-- ============================================
-- 1. USERS & AUTHENTICATION
-- ============================================

CREATE TABLE Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Doctor', 'Receptionist', 'Accountant', 'Nurse') NOT NULL,
    email VARCHAR(100) UNIQUE,
    status ENUM('Active', 'Inactive', 'Suspended') DEFAULT 'Active',
    last_login DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB;

-- ============================================
-- 2. DEPARTMENTS
-- ============================================

CREATE TABLE Departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    email VARCHAR(100),
    floor INT,
    description TEXT,
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dept_name (department_name)
) ENGINE=InnoDB;

-- ============================================
-- 3. STAFF MANAGEMENT
-- ============================================

CREATE TABLE Staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    department_id INT,
    full_name VARCHAR(100) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    qualification VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    salary DECIMAL(10, 2),
    joining_date DATE NOT NULL,
    status ENUM('Active', 'On Leave', 'Resigned', 'Terminated') DEFAULT 'Active',
    emergency_contact VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES Departments(department_id) ON DELETE SET NULL,
    INDEX idx_staff_name (full_name),
    INDEX idx_designation (designation)
) ENGINE=InnoDB;

-- Add department head reference
ALTER TABLE Departments ADD COLUMN head_id INT;
ALTER TABLE Departments ADD FOREIGN KEY (head_id) REFERENCES Staff(staff_id) ON DELETE SET NULL;

-- ============================================
-- 4. DOCTORS
-- ============================================

CREATE TABLE Doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    staff_id INT UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    qualification VARCHAR(200) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    experience_years INT,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    consultation_fee DECIMAL(10, 2) NOT NULL,
    available_days VARCHAR(100),
    available_from TIME,
    available_to TIME,
    status ENUM('Available', 'On Leave', 'Busy', 'Unavailable') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id) ON DELETE CASCADE,
    INDEX idx_specialization (specialization),
    INDEX idx_doctor_name (full_name)
) ENGINE=InnoDB;

-- ============================================
-- 5. PATIENTS
-- ============================================

CREATE TABLE Patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    id_type ENUM('CNIC', 'Passport', 'Driving License') NOT NULL,
    id_number VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    age INT,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    country VARCHAR(50) DEFAULT 'Pakistan',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Active', 'Discharged', 'Deceased', 'Inactive') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_patient (id_type, id_number),
    INDEX idx_patient_name (full_name),
    INDEX idx_phone (phone),
    INDEX idx_id_number (id_number)
) ENGINE=InnoDB;

-- ============================================
-- 6. MEDICAL HISTORY
-- ============================================

CREATE TABLE Medical_History (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    allergies TEXT,
    chronic_conditions TEXT,
    previous_surgeries TEXT,
    current_medications TEXT,
    family_medical_history TEXT,
    smoking_status ENUM('Never', 'Former', 'Current'),
    alcohol_consumption ENUM('None', 'Occasional', 'Regular'),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id)
) ENGINE=InnoDB;

-- ============================================
-- 7. ROOMS (FIXED TABLE NAME)
-- ============================================

CREATE TABLE Rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) UNIQUE NOT NULL,
    room_type ENUM('General', 'Private', 'ICU', 'NICU', 'Emergency', 'Operation Theater') NOT NULL,
    floor INT,
    capacity INT DEFAULT 1,
    price_per_day DECIMAL(10, 2) NOT NULL,
    availability_status ENUM('Available', 'Occupied', 'Under Maintenance', 'Reserved') DEFAULT 'Available',
    amenities TEXT,
    last_cleaned DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_room_type (room_type),
    INDEX idx_availability (availability_status)
) ENGINE=InnoDB;

-- ============================================
-- 8. ADMISSIONS
-- ============================================

CREATE TABLE Admissions (
    admission_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    room_id INT NOT NULL,
    admitting_doctor_id INT NOT NULL,
    admission_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expected_discharge_date DATE,
    actual_discharge_date DATETIME,
    diagnosis TEXT NOT NULL,
    admission_type ENUM('Emergency', 'Planned', 'Transfer') DEFAULT 'Planned',
    initial_deposit DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status ENUM('Admitted', 'Discharged', 'Transferred', 'Deceased') DEFAULT 'Admitted',
    discharge_summary TEXT,
    discharge_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE RESTRICT,
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id) ON DELETE RESTRICT,
    FOREIGN KEY (admitting_doctor_id) REFERENCES Doctors(doctor_id) ON DELETE RESTRICT,
    INDEX idx_patient (patient_id),
    INDEX idx_admission_date (admission_date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- 9. APPOINTMENTS
-- ============================================

CREATE TABLE Appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    appointment_type ENUM('Consultation', 'Follow-up', 'Emergency', 'Check-up') DEFAULT 'Consultation',
    status ENUM('Scheduled', 'Confirmed', 'Completed', 'Cancelled', 'No-Show') DEFAULT 'Scheduled',
    reason TEXT,
    diagnosis TEXT,
    prescription TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE RESTRICT,
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_doctor_date (doctor_id, appointment_date),
    INDEX idx_patient (patient_id),
    UNIQUE KEY unique_appointment (doctor_id, appointment_date, appointment_time)
) ENGINE=InnoDB;

-- ============================================
-- 10. BILLING
-- ============================================

CREATE TABLE Bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    admission_id INT,
    bill_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(10, 2) DEFAULT 0.00,
    pending_amount DECIMAL(10, 2) DEFAULT 0.00,
    payment_status ENUM('Pending', 'Partially Paid', 'Fully Paid', 'Overdue') DEFAULT 'Pending',
    bill_type ENUM('Admission', 'Consultation', 'Emergency', 'Other') DEFAULT 'Admission',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE RESTRICT,
    FOREIGN KEY (admission_id) REFERENCES Admissions(admission_id) ON DELETE SET NULL,
    INDEX idx_patient (patient_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_bill_date (bill_date)
) ENGINE=InnoDB;

-- ============================================
-- 11. BILL ITEMS
-- ============================================

CREATE TABLE Bill_Items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    service_type ENUM('Room Charge', 'Consultation', 'Medicine', 'Lab Test', 'Surgery', 'Equipment', 'Other') NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    service_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES Bills(bill_id) ON DELETE CASCADE,
    INDEX idx_bill (bill_id),
    INDEX idx_service_type (service_type)
) ENGINE=InnoDB;

-- ============================================
-- 12. PAYMENTS
-- ============================================

CREATE TABLE Payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Cash', 'Card', 'Bank Transfer', 'Cheque', 'Insurance', 'Online') NOT NULL,
    transaction_reference VARCHAR(100),
    received_by INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES Bills(bill_id) ON DELETE RESTRICT,
    FOREIGN KEY (received_by) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_bill (bill_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB;



-- ============================================
-- 15. PRESCRIPTIONS
-- ============================================

CREATE TABLE Prescriptions (
    prescription_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_id INT,
    admission_id INT,
    prescription_date DATE NOT NULL,
    diagnosis TEXT,
    notes TEXT,
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE RESTRICT,
    FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id) ON DELETE SET NULL,
    FOREIGN KEY (admission_id) REFERENCES Admissions(admission_id) ON DELETE SET NULL,
    INDEX idx_patient (patient_id),
    INDEX idx_date (prescription_date)
) ENGINE=InnoDB;

-- ============================================
-- 16. PRESCRIPTION ITEMS
-- ============================================

CREATE TABLE Prescription_Items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    prescription_id INT NOT NULL,
    medicine_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    FOREIGN KEY (prescription_id) REFERENCES Prescriptions(prescription_id) ON DELETE CASCADE,
    INDEX idx_prescription (prescription_id)
) ENGINE=InnoDB;

-- ============================================
-- 17. LAB TESTS
-- ============================================

CREATE TABLE Lab_Tests (
    test_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT,
    test_name VARCHAR(200) NOT NULL,
    test_type VARCHAR(100),
    test_date DATE NOT NULL,
    report_date DATE,
    result TEXT,
    status ENUM('Pending', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Pending',
    cost DECIMAL(10, 2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE SET NULL,
    INDEX idx_patient (patient_id),
    INDEX idx_test_date (test_date)
) ENGINE=InnoDB;

-- ============================================
-- 18. INVENTORY
-- ============================================

CREATE TABLE Inventory (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(200) NOT NULL,
    item_type ENUM('Medicine', 'Equipment', 'Consumable', 'Other') NOT NULL,
    category VARCHAR(100),
    quantity_available INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10, 2),
    reorder_level INT DEFAULT 10,
    expiry_date DATE,
    supplier VARCHAR(100),
    status ENUM('Available', 'Low Stock', 'Out of Stock', 'Expired') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item_name (item_name),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- 19. AUDIT LOG
-- ============================================

CREATE TABLE Audit_Log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action_type ENUM('INSERT', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'VIEW') NOT NULL,
    table_name VARCHAR(100),
    record_id INT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_action (action_type),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB;

-- ============================================
-- VIEWS
-- ============================================

CREATE VIEW v_available_rooms AS
SELECT 
    r.room_id,
    r.room_number,
    r.room_type,
    r.floor,
    r.capacity,
    r.price_per_day,
    r.amenities,
    r.availability_status
FROM Rooms r
WHERE r.availability_status = 'Available';

CREATE VIEW v_current_admissions AS
SELECT 
    a.admission_id,
    a.admission_date,
    p.patient_id,
    p.full_name AS patient_name,
    p.phone AS patient_phone,
    r.room_number,
    r.room_type,
    d.full_name AS doctor_name,
    d.specialization,
    a.diagnosis,
    a.initial_deposit,
    a.status
FROM Admissions a
JOIN Patients p ON a.patient_id = p.patient_id
JOIN Rooms r ON a.room_id = r.room_id
JOIN Doctors d ON a.admitting_doctor_id = d.doctor_id
WHERE a.status = 'Admitted';

CREATE VIEW v_patient_summary AS
SELECT 
    p.patient_id,
    p.full_name,
    p.age,
    p.gender,
    p.blood_group,
    p.phone,
    p.email,
    p.address,
    mh.allergies,
    mh.chronic_conditions,
    COUNT(DISTINCT a.admission_id) AS total_admissions,
    COUNT(DISTINCT ap.appointment_id) AS total_appointments
FROM Patients p
LEFT JOIN Medical_History mh ON p.patient_id = mh.patient_id
LEFT JOIN Admissions a ON p.patient_id = a.patient_id
LEFT JOIN Appointments ap ON p.patient_id = ap.patient_id
GROUP BY p.patient_id, p.full_name, p.age, p.gender, p.blood_group, 
         p.phone, p.email, p.address, mh.allergies, mh.chronic_conditions;

CREATE VIEW v_doctor_schedule AS
SELECT 
    d.doctor_id,
    d.full_name AS doctor_name,
    d.specialization,
    ap.appointment_id,
    ap.appointment_date,
    ap.appointment_time,
    p.full_name AS patient_name,
    p.phone AS patient_phone,
    ap.status,
    ap.appointment_type
FROM Doctors d
LEFT JOIN Appointments ap ON d.doctor_id = ap.doctor_id
LEFT JOIN Patients p ON ap.patient_id = p.patient_id
WHERE ap.appointment_date >= CURDATE()
ORDER BY ap.appointment_date, ap.appointment_time;

CREATE VIEW v_financial_summary AS
SELECT 
    b.bill_id,
    b.bill_date,
    p.full_name AS patient_name,
    b.bill_type,
    b.total_amount,
    b.paid_amount,
    b.pending_amount,
    b.payment_status
FROM Bills b
JOIN Patients p ON b.patient_id = p.patient_id
ORDER BY b.bill_date DESC;

CREATE VIEW v_department_stats AS
SELECT 
    d.department_id,
    d.department_name,
    COUNT(DISTINCT s.staff_id) AS total_staff,
    s2.full_name AS department_head,
    d.phone AS department_phone
FROM Departments d
LEFT JOIN Staff s ON d.department_id = s.department_id AND s.status = 'Active'
LEFT JOIN Staff s2 ON d.head_id = s2.staff_id
GROUP BY d.department_id, d.department_name, s2.full_name, d.phone;

CREATE VIEW v_ambulance_availability AS
SELECT 
    ambulance_id,
    vehicle_number,
    ambulance_type,
    driver_name,
    driver_phone,
    status,
    next_maintenance_date
FROM Ambulances
WHERE status IN ('Available', 'On Call')
ORDER BY status, ambulance_type;

CREATE VIEW v_pending_payments AS
SELECT 
    b.bill_id,
    b.bill_date,
    p.patient_id,
    p.full_name AS patient_name,
    p.phone AS patient_phone,
    b.final_amount,
    b.paid_amount,
    b.pending_amount,
    DATEDIFF(CURDATE(), b.bill_date) AS days_pending
FROM Bills b
JOIN Patients p ON b.patient_id = p.patient_id
WHERE b.payment_status IN ('Pending', 'Partially Paid', 'Overdue')
ORDER BY days_pending DESC;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

CREATE PROCEDURE sp_admit_patient(
    IN p_patient_id INT,
    IN p_room_id INT,
    IN p_doctor_id INT,
    IN p_diagnosis TEXT,
    IN p_admission_type VARCHAR(20),
    IN p_initial_deposit DECIMAL(10,2)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Check if room is available
    IF (SELECT availability_status FROM Rooms WHERE room_id = p_room_id) != 'Available' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Room is not available';
    END IF;
    
    -- Insert admission record
    INSERT INTO Admissions (patient_id, room_id, admitting_doctor_id, diagnosis, 
                           admission_type, initial_deposit, status)
    VALUES (p_patient_id, p_room_id, p_doctor_id, p_diagnosis, 
            p_admission_type, p_initial_deposit, 'Admitted');
    
    -- Update room status
    UPDATE Rooms SET availability_status = 'Occupied' WHERE room_id = p_room_id;
    
    -- Update patient status
    UPDATE Patients SET status = 'Active' WHERE patient_id = p_patient_id;
    
    COMMIT;
END //

CREATE PROCEDURE sp_discharge_patient(
    IN p_admission_id INT,
    IN p_discharge_summary TEXT,
    IN p_discharge_instructions TEXT
)
BEGIN
    DECLARE v_room_id INT;
    DECLARE v_patient_id INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Get room and patient details
    SELECT room_id, patient_id INTO v_room_id, v_patient_id
    FROM Admissions WHERE admission_id = p_admission_id;
    
    -- Update admission record
    UPDATE Admissions 
    SET actual_discharge_date = NOW(),
        status = 'Discharged',
        discharge_summary = p_discharge_summary,
        discharge_instructions = p_discharge_instructions
    WHERE admission_id = p_admission_id;
    
    -- Update room status
    UPDATE Rooms SET availability_status = 'Available' WHERE room_id = v_room_id;
    
    -- Update patient status
    UPDATE Patients SET status = 'Discharged' WHERE patient_id = v_patient_id;
    
    COMMIT;
END //

CREATE PROCEDURE sp_generate_bill(
    IN p_admission_id INT,
    OUT p_bill_id INT
)
BEGIN
    DECLARE v_patient_id INT;
    DECLARE v_room_id INT;
    DECLARE v_admission_date DATETIME;
    DECLARE v_discharge_date DATETIME;
    DECLARE v_days INT;
    DECLARE v_room_charge DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_initial_deposit DECIMAL(10,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Get admission details
    SELECT patient_id, room_id, admission_date, actual_discharge_date, initial_deposit
    INTO v_patient_id, v_room_id, v_admission_date, v_discharge_date, v_initial_deposit
    FROM Admissions WHERE admission_id = p_admission_id;
    
    -- Calculate days
    SET v_days = DATEDIFF(IFNULL(v_discharge_date, NOW()), v_admission_date);
    IF v_days < 1 THEN SET v_days = 1; END IF;
    
    -- Get room price
    SELECT price_per_day INTO v_room_charge FROM Rooms WHERE room_id = v_room_id;
    
    -- Calculate total room charges
    SET v_total = v_room_charge * v_days;
    
    -- Create bill
    INSERT INTO Bills (patient_id, admission_id, bill_type, total_amount, 
                      final_amount, paid_amount, pending_amount, payment_status)
    VALUES (v_patient_id, p_admission_id, 'Admission', v_total, 
            v_total, v_initial_deposit, v_total - v_initial_deposit, 
            IF(v_initial_deposit >= v_total, 'Fully Paid', 'Partially Paid'));
    
    SET p_bill_id = LAST_INSERT_ID();
    
    -- Add room charges to bill items
    INSERT INTO Bill_Items (bill_id, service_type, description, quantity, unit_price, total_price)
    VALUES (p_bill_id, 'Room Charge', CONCAT('Room charges for ', v_days, ' days'), 
            v_days, v_room_charge, v_total);
    
    COMMIT;
END //

CREATE PROCEDURE sp_record_payment(
    IN p_bill_id INT,
    IN p_amount DECIMAL(10,2),
    IN p_payment_method VARCHAR(20),
    IN p_transaction_ref VARCHAR(100),
    IN p_received_by INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Insert payment record
    INSERT INTO Payments (bill_id, amount, payment_method, transaction_reference, received_by)
    VALUES (p_bill_id, p_amount, p_payment_method, p_transaction_ref, p_received_by);
    
    -- Update bill amounts
    UPDATE Bills 
    SET paid_amount = paid_amount + p_amount,
        pending_amount = final_amount - (paid_amount + p_amount),
        payment_status = CASE 
            WHEN (paid_amount + p_amount) >= final_amount THEN 'Fully Paid'
            WHEN (paid_amount + p_amount) > 0 THEN 'Partially Paid'
            ELSE 'Pending'
        END
    WHERE bill_id = p_bill_id;
    
    COMMIT;
END //

CREATE PROCEDURE sp_monthly_revenue(
    IN p_month INT,
    IN p_year INT
)
BEGIN
    SELECT 
        DATE(bill_date) AS date,
        COUNT(*) AS total_bills,
        SUM(final_amount) AS total_revenue,
        SUM(paid_amount) AS collected_amount,
        SUM(pending_amount) AS pending_amount,
        bill_type
    FROM Bills
    WHERE MONTH(bill_date) = p_month AND YEAR(bill_date) = p_year
    GROUP BY DATE(bill_date), bill_type
    ORDER BY date DESC;
END //

DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER //

CREATE TRIGGER trg_after_admission_insert
AFTER INSERT ON Admissions
FOR EACH ROW
BEGIN
    UPDATE Rooms 
    SET availability_status = 'Occupied' 
    WHERE room_id = NEW.room_id;
END //

CREATE TRIGGER trg_after_admission_discharge
AFTER UPDATE ON Admissions
FOR EACH ROW
BEGIN
    IF NEW.status = 'Discharged' AND OLD.status != 'Discharged' THEN
        UPDATE Rooms 
        SET availability_status = 'Available' 
        WHERE room_id = NEW.room_id;
    END IF;
END //

CREATE TRIGGER trg_calculate_bill_total
AFTER INSERT ON Bill_Items
FOR EACH ROW
BEGIN
    UPDATE Bills 
    SET total_amount = total_amount + NEW.total_price,
        final_amount = total_amount + NEW.total_price - discount_amount + tax_amount,
        pending_amount = final_amount - paid_amount
    WHERE bill_id = NEW.bill_id;
END //

CREATE TRIGGER trg_prevent_double_booking
BEFORE INSERT ON Appointments
FOR EACH ROW
BEGIN
    DECLARE v_count INT;
    
    SELECT COUNT(*) INTO v_count
    FROM Appointments
    WHERE doctor_id = NEW.doctor_id
    AND appointment_date = NEW.appointment_date
    AND appointment_time = NEW.appointment_time
    AND status NOT IN ('Cancelled', 'Completed');
    
    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'This time slot is already booked';
    END IF;
END //

CREATE TRIGGER trg_audit_patient_update
AFTER UPDATE ON Patients
FOR EACH ROW
BEGIN
    INSERT INTO Audit_Log (user_id, action_type, table_name, record_id, 
                          old_value, new_value, description)
    VALUES (NULL, 'UPDATE', 'Patients', NEW.patient_id,
            CONCAT('Name: ', OLD.full_name, ', Phone: ', OLD.phone),
            CONCAT('Name: ', NEW.full_name, ', Phone: ', NEW.phone),
            'Patient information updated');
END //

CREATE TRIGGER trg_update_inventory_status
BEFORE UPDATE ON Inventory
FOR EACH ROW
BEGIN
    IF NEW.quantity_available = 0 THEN
        SET NEW.status = 'Out of Stock';
    ELSEIF NEW.quantity_available <= NEW.reorder_level THEN
        SET NEW.status = 'Low Stock';
    ELSEIF NEW.expiry_date < CURDATE() THEN
        SET NEW.status = 'Expired';
    ELSE
        SET NEW.status = 'Available';
    END IF;
END //

DELIMITER ;

-- ============================================
-- ADDITIONAL INDEXES
-- ============================================

CREATE INDEX idx_admission_patient_date ON Admissions(patient_id, admission_date);
CREATE INDEX idx_bill_patient_status ON Bills(patient_id, payment_status);
CREATE INDEX idx_appointment_doctor_date_time ON Appointments(doctor_id, appointment_date, appointment_time);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Insert Users
INSERT INTO Users (username, password_hash, role, email, status) VALUES
('admin', SHA2('admin123', 256), 'Admin', 'admin@hospital.com', 'Active'),
('reception1', SHA2('reception123', 256), 'Receptionist', 'reception@hospital.com', 'Active'),
('doctor1', SHA2('doctor123', 256), 'Doctor', 'doctor1@hospital.com', 'Active'),
('doctor2', SHA2('doctor123', 256), 'Doctor', 'doctor2@hospital.com', 'Active'),
('doctor3', SHA2('doctor123', 256), 'Doctor', 'doctor3@hospital.com', 'Active'),
('doctor4', SHA2('doctor123', 256), 'Doctor', 'doctor4@hospital.com', 'Active'),
('doctor5', SHA2('doctor123', 256), 'Doctor', 'doctor5@hospital.com', 'Active'),
('nurse1', SHA2('nurse123', 256), 'Nurse', 'nurse1@hospital.com', 'Active'),
('nurse2', SHA2('nurse123', 256), 'Nurse', 'nurse2@hospital.com', 'Active'),
('accountant1', SHA2('account123', 256), 'Accountant', 'accountant@hospital.com', 'Active'),
('reception2', SHA2('reception123', 256), 'Receptionist', 'reception2@hospital.com', 'Active');

-- Insert Departments
INSERT INTO Departments (department_name, phone, email, floor) VALUES
('Emergency', '051-1111111', 'emergency@hospital.com', 0),
('Cardiology', '051-2222222', 'cardiology@hospital.com', 2),
('Neurology', '051-3333333', 'neurology@hospital.com', 3),
('Pediatrics', '051-4444444', 'pediatrics@hospital.com', 1),
('Orthopedics', '051-5555555', 'orthopedics@hospital.com', 2);

-- Insert Rooms
INSERT INTO Rooms (room_number, room_type, floor, capacity, price_per_day, availability_status) VALUES
('101', 'General', 1, 4, 500.00, 'Available'),
('102', 'General', 1, 4, 500.00, 'Available'),
('201', 'Private', 2, 1, 1500.00, 'Available'),
('202', 'Private', 2, 1, 1500.00, 'Available'),
('301', 'ICU', 3, 1, 3500.00, 'Available'),
('302', 'ICU', 3, 1, 3500.00, 'Available'),
('303', 'ICU', 3, 1, 3500.00, 'Available');

-- Insert Doctors
INSERT INTO Doctors (user_id, full_name, specialization, qualification, license_number, 
                    phone, email, consultation_fee, status) VALUES
(3, 'Dr. Ahmed Khan', 'Cardiologist', 'MBBS, MD Cardiology', 'PMC-12345', 
 '0300-1234567', 'ahmed.khan@hospital.com', 2000.00, 'Available'),
(4, 'Dr. Sarah Ali', 'Pediatrician', 'MBBS, DCH', 'PMC-23456',
 '0300-2345678', 'sarah.ali@hospital.com', 1500.00, 'Available'),
(5, 'Dr. Hassan Raza', 'Neurologist', 'MBBS, FCPS Neurology', 'PMC-34567',
 '0300-3456789', 'hassan.raza@hospital.com', 2500.00, 'Available'),
(6, 'Dr. Ayesha Mahmood', 'Endocrinologist', 'MBBS, MRCP', 'PMC-45678',
 '0300-4567890', 'ayesha.mahmood@hospital.com', 2000.00, 'Available'),
(7, 'Dr. Usman Khalid', 'Orthopedic Surgeon', 'MBBS, MS Orthopedics', 'PMC-56789',
 '0300-5678901', 'usman.khalid@hospital.com', 3000.00, 'Available');

-- Insert Ambulances
INSERT INTO Ambulances (vehicle_number, ambulance_type, driver_name, driver_phone, status) VALUES
('AMB-001', 'Basic Life Support', 'Ali Hassan', '0300-9876543', 'Available'),
('AMB-002', 'Advanced Life Support', 'Usman Ahmed', '0300-8765432', 'Available');

-- ============================================
-- END OF CORRECTED SCHEMA
-- ============================================

-- ============================================
-- CLEAR EXISTING SAMPLE DATA
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Audit_Log;
TRUNCATE TABLE Prescription_Items;
TRUNCATE TABLE Prescriptions;
TRUNCATE TABLE Lab_Tests;
TRUNCATE TABLE Ambulance_Bookings;
TRUNCATE TABLE Ambulances;
TRUNCATE TABLE Payments;
TRUNCATE TABLE Bill_Items;
TRUNCATE TABLE Bills;
TRUNCATE TABLE Appointments;
TRUNCATE TABLE Admissions;
TRUNCATE TABLE Medical_History;
TRUNCATE TABLE Inventory;
TRUNCATE TABLE Doctors;
TRUNCATE TABLE Staff;
TRUNCATE TABLE Patients;
TRUNCATE TABLE Rooms;
TRUNCATE TABLE Departments;
TRUNCATE TABLE Users;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- 1. USERS
INSERT INTO Users (username, password_hash, role, email, status, last_login) VALUES
('admin', SHA2('admin123', 256), 'Admin', 'admin@hospital.com', 'Active', '2025-11-04 08:00:00'),
('dr.ahmed', SHA2('doctor123', 256), 'Doctor', 'ahmed.khan@hospital.com', 'Active', '2025-11-04 09:15:00'),
('dr.sarah', SHA2('doctor123', 256), 'Doctor', 'sarah.ali@hospital.com', 'Active', '2025-11-04 08:30:00'),
('dr.hassan', SHA2('doctor123', 256), 'Doctor', 'hassan.raza@hospital.com', 'Active', '2025-11-04 10:00:00'),
('dr.ayesha', SHA2('doctor123', 256), 'Doctor', 'ayesha.mahmood@hospital.com', 'Active', '2025-11-03 14:00:00'),
('dr.usman', SHA2('doctor123', 256), 'Doctor', 'usman.khalid@hospital.com', 'Active', '2025-11-04 07:45:00'),
('dr.fatima', SHA2('doctor123', 256), 'Doctor', 'fatima.sheikh@hospital.com', 'Active', '2025-11-04 09:00:00'),
('dr.bilal', SHA2('doctor123', 256), 'Doctor', 'bilal.ahmed@hospital.com', 'Active', '2025-11-03 16:30:00'),
('reception1', SHA2('reception123', 256), 'Receptionist', 'reception1@hospital.com', 'Active', '2025-11-04 08:00:00'),
('reception2', SHA2('reception123', 256), 'Receptionist', 'reception2@hospital.com', 'Active', '2025-11-04 08:00:00'),
('nurse.aisha', SHA2('nurse123', 256), 'Nurse', 'aisha.khan@hospital.com', 'Active', '2025-11-04 07:00:00'),
('nurse.maria', SHA2('nurse123', 256), 'Nurse', 'maria.ali@hospital.com', 'Active', '2025-11-04 07:00:00'),
('nurse.zainab', SHA2('nurse123', 256), 'Nurse', 'zainab.hassan@hospital.com', 'Active', '2025-11-04 15:00:00'),
('accountant1', SHA2('account123', 256), 'Accountant', 'accounts@hospital.com', 'Active', '2025-11-04 09:00:00'),
('accountant2', SHA2('account123', 256), 'Accountant', 'billing@hospital.com', 'Active', '2025-11-04 09:00:00');

-- 2. DEPARTMENTS
INSERT INTO Departments (department_name, phone, email, floor, description, status) VALUES
('Emergency', '051-1111111', 'emergency@hospital.com', 0, '24/7 Emergency Services', 'Active'),
('Cardiology', '051-2222222', 'cardiology@hospital.com', 2, 'Heart and Cardiovascular Care', 'Active'),
('Neurology', '051-3333333', 'neurology@hospital.com', 3, 'Brain and Nervous System', 'Active'),
('Pediatrics', '051-4444444', 'pediatrics@hospital.com', 1, 'Child Healthcare', 'Active'),
('Orthopedics', '051-5555555', 'orthopedics@hospital.com', 2, 'Bone and Joint Care', 'Active'),
('General Medicine', '051-6666666', 'general@hospital.com', 1, 'General Healthcare Services', 'Active'),
('Surgery', '051-7777777', 'surgery@hospital.com', 3, 'Surgical Procedures', 'Active'),
('Radiology', '051-8888888', 'radiology@hospital.com', 0, 'Imaging and Diagnostics', 'Active');

-- 3. STAFF
INSERT INTO Staff (user_id, department_id, full_name, designation, qualification, phone, email, date_of_birth, gender, salary, joining_date, status, emergency_contact) VALUES
(1, NULL, 'Muhammad Asif', 'Administrator', 'MBA Healthcare Management', '0300-1111111', 'admin@hospital.com', '1980-05-15', 'Male', 150000.00, '2020-01-01', 'Active', '0301-1111111'),
(9, 1, 'Sana Malik', 'Receptionist', 'Intermediate', '0300-2222222', 'reception1@hospital.com', '1995-08-20', 'Female', 35000.00, '2022-06-01', 'Active', '0301-2222222'),
(10, 4, 'Hina Tariq', 'Receptionist', 'Intermediate', '0300-3333333', 'reception2@hospital.com', '1996-03-10', 'Female', 35000.00, '2023-01-15', 'Active', '0301-3333333'),
(11, 1, 'Aisha Khan', 'Senior Nurse', 'BSN', '0300-4444444', 'aisha.khan@hospital.com', '1990-07-12', 'Female', 55000.00, '2021-03-01', 'Active', '0301-4444444'),
(12, 4, 'Maria Ali', 'Nurse', 'BSN', '0300-5555555', 'maria.ali@hospital.com', '1993-11-25', 'Female', 45000.00, '2022-09-01', 'Active', '0301-5555555'),
(13, 2, 'Zainab Hassan', 'Nurse', 'BSN', '0300-6666666', 'zainab.hassan@hospital.com', '1994-02-18', 'Female', 45000.00, '2023-02-01', 'Active', '0301-6666666'),
(14, NULL, 'Kamran Iqbal', 'Senior Accountant', 'ACCA', '0300-7777777', 'accounts@hospital.com', '1985-09-30', 'Male', 85000.00, '2020-06-01', 'Active', '0301-7777777'),
(15, NULL, 'Nadia Hussain', 'Billing Officer', 'B.Com', '0300-8888888', 'billing@hospital.com', '1992-12-05', 'Female', 50000.00, '2022-11-01', 'Active', '0301-8888888');

-- 4. DOCTORS
INSERT INTO Doctors (user_id, staff_id, full_name, specialization, qualification, license_number, experience_years, phone, email, consultation_fee, available_days, available_from, available_to, status) VALUES
(2, NULL, 'Dr. Ahmed Khan', 'Cardiologist', 'MBBS, MD Cardiology', 'PMC-12345', 15, '0300-1234567', 'ahmed.khan@hospital.com', 2500.00, 'Mon,Tue,Wed,Thu,Fri', '09:00:00', '17:00:00', 'Available'),
(3, NULL, 'Dr. Sarah Ali', 'Pediatrician', 'MBBS, DCH, FCPS', 'PMC-23456', 10, '0300-2345678', 'sarah.ali@hospital.com', 2000.00, 'Mon,Tue,Wed,Thu,Fri,Sat', '10:00:00', '18:00:00', 'Available'),
(4, NULL, 'Dr. Hassan Raza', 'Neurologist', 'MBBS, FCPS Neurology', 'PMC-34567', 12, '0300-3456789', 'hassan.raza@hospital.com', 3000.00, 'Mon,Wed,Fri', '11:00:00', '16:00:00', 'Available'),
(5, NULL, 'Dr. Ayesha Mahmood', 'General Physician', 'MBBS, FCPS Medicine', 'PMC-45678', 8, '0300-4567890', 'ayesha.mahmood@hospital.com', 1500.00, 'Mon,Tue,Wed,Thu,Fri', '08:00:00', '16:00:00', 'Available'),
(6, NULL, 'Dr. Usman Khalid', 'Orthopedic Surgeon', 'MBBS, MS Orthopedics', 'PMC-56789', 14, '0300-5678901', 'usman.khalid@hospital.com', 3500.00, 'Tue,Thu,Sat', '10:00:00', '15:00:00', 'Available'),
(7, NULL, 'Dr. Fatima Sheikh', 'Dermatologist', 'MBBS, FCPS Dermatology', 'PMC-67890', 9, '0300-6789012', 'fatima.sheikh@hospital.com', 2000.00, 'Mon,Tue,Wed,Thu,Fri', '09:00:00', '17:00:00', 'Available'),
(8, NULL, 'Dr. Bilal Ahmed', 'General Surgeon', 'MBBS, FCPS Surgery', 'PMC-78901', 11, '0300-7890123', 'bilal.ahmed@hospital.com', 2800.00, 'Mon,Wed,Thu,Fri', '08:00:00', '14:00:00', 'Available');

-- 5. ROOMS
INSERT INTO Rooms (room_number, room_type, floor, capacity, price_per_day, availability_status, amenities, last_cleaned) VALUES
('101', 'General', 1, 4, 800.00, 'Available', 'AC, TV, Shared Bathroom', '2025-11-04 06:00:00'),
('102', 'General', 1, 4, 800.00, 'Available', 'AC, TV, Shared Bathroom', '2025-11-04 06:00:00'),
('103', 'General', 1, 4, 800.00, 'Available', 'AC, TV, Shared Bathroom', '2025-11-04 06:00:00'),
('104', 'General', 1, 4, 800.00, 'Occupied', 'AC, TV, Shared Bathroom', '2025-11-03 06:00:00'),
('201', 'Private', 2, 1, 2000.00, 'Available', 'AC, TV, Private Bathroom, Refrigerator', '2025-11-04 07:00:00'),
('202', 'Private', 2, 1, 2000.00, 'Occupied', 'AC, TV, Private Bathroom, Refrigerator', '2025-11-03 07:00:00'),
('203', 'Private', 2, 1, 2000.00, 'Available', 'AC, TV, Private Bathroom, Refrigerator', '2025-11-04 07:00:00'),
('204', 'Private', 2, 1, 2000.00, 'Occupied', 'AC, TV, Private Bathroom, Refrigerator', '2025-11-02 07:00:00'),
('301', 'ICU', 3, 1, 5000.00, 'Available', 'Advanced Monitoring, Ventilator Support', '2025-11-04 08:00:00'),
('302', 'ICU', 3, 1, 5000.00, 'Occupied', 'Advanced Monitoring, Ventilator Support', '2025-11-03 08:00:00'),
('303', 'ICU', 3, 1, 5000.00, 'Available', 'Advanced Monitoring, Ventilator Support', '2025-11-04 08:00:00'),
('E01', 'Emergency', 0, 2, 1500.00, 'Available', 'Emergency Equipment', '2025-11-04 05:00:00'),
('E02', 'Emergency', 0, 2, 1500.00, 'Available', 'Emergency Equipment', '2025-11-04 05:00:00'),
('OT1', 'Operation Theater', 3, 1, 8000.00, 'Available', 'Full Surgical Setup', '2025-11-04 09:00:00'),
('OT2', 'Operation Theater', 3, 1, 8000.00, 'Under Maintenance', 'Full Surgical Setup', '2025-11-03 09:00:00');

-- 6. PATIENTS
INSERT INTO Patients (id_type, id_number, full_name, date_of_birth, age, gender, blood_group, phone, email, emergency_contact_name, emergency_contact_phone, address, city, country, status) VALUES
('CNIC', '42101-1234567-1', 'Ali Hassan', '1985-03-15', 40, 'Male', 'B+', '0321-1111111', 'ali.hassan@email.com', 'Fatima Hassan', '0322-1111111', 'House 123, Street 5, F-10', 'Islamabad', 'Pakistan', 'Active'),
('CNIC', '42201-2345678-2', 'Ayesha Khan', '1990-07-22', 35, 'Female', 'A+', '0321-2222222', 'ayesha.k@email.com', 'Ahmed Khan', '0322-2222222', 'Flat 45, Block B, Gulberg', 'Karachi', 'Pakistan', 'Active'),
('CNIC', '42301-3456789-3', 'Muhammad Bilal', '1978-11-10', 46, 'Male', 'O+', '0321-3333333', 'bilal.m@email.com', 'Sana Bilal', '0322-3333333', 'Villa 67, DHA Phase 5', 'Lahore', 'Pakistan', 'Active'),
('Passport', 'AB1234567', 'Sara Ahmed', '1995-05-18', 30, 'Female', 'AB+', '0321-4444444', 'sara.ahmed@email.com', 'Hassan Ahmed', '0322-4444444', 'Apartment 12, Clifton', 'Karachi', 'Pakistan', 'Active'),
('CNIC', '42401-5678901-5', 'Usman Malik', '2015-09-25', 10, 'Male', 'A-', '0321-5555555', 'malik.family@email.com', 'Hina Malik', '0322-5555555', 'House 89, Satellite Town', 'Rawalpindi', 'Pakistan', 'Active'),
('CNIC', '42501-6789012-6', 'Zainab Ali', '1988-01-30', 37, 'Female', 'B-', '0321-6666666', 'zainab.ali@email.com', 'Hassan Ali', '0322-6666666', 'House 234, G-11', 'Islamabad', 'Pakistan', 'Discharged'),
('CNIC', '42601-7890123-7', 'Kamran Hussain', '1975-12-05', 49, 'Male', 'O-', '0321-7777777', 'kamran.h@email.com', 'Nadia Hussain', '0322-7777777', 'Plot 45, Sector E', 'Islamabad', 'Pakistan', 'Active'),
('CNIC', '42701-8901234-8', 'Fatima Zahra', '2010-06-15', 15, 'Female', 'A+', '0321-8888888', 'zahra.family@email.com', 'Ali Zahra', '0322-8888888', 'House 156, Bahria Town', 'Karachi', 'Pakistan', 'Active'),
('CNIC', '42801-9012345-9', 'Imran Sheikh', '1982-08-20', 43, 'Male', 'AB-', '0321-9999999', 'imran.sheikh@email.com', 'Sadia Sheikh', '0322-9999999', 'Flat 78, Gulshan', 'Karachi', 'Pakistan', 'Active'),
('CNIC', '42901-0123456-0', 'Maryam Siddique', '1992-04-12', 33, 'Female', 'B+', '0321-0000000', 'maryam.s@email.com', 'Ahmed Siddique', '0322-0000000', 'House 90, Model Town', 'Lahore', 'Pakistan', 'Active'),
('CNIC', '43001-1234567-1', 'Tariq Aziz', '1970-10-08', 55, 'Male', 'O+', '0333-1111111', 'tariq.aziz@email.com', 'Shabana Aziz', '0333-2222222', 'House 45, F-7', 'Islamabad', 'Pakistan', 'Active'),
('CNIC', '43101-2345678-2', 'Hira Farooq', '1998-02-28', 27, 'Female', 'A+', '0333-3333333', 'hira.f@email.com', 'Farooq Ahmed', '0333-4444444', 'Apartment 23, Johar Town', 'Lahore', 'Pakistan', 'Active');

-- 7. MEDICAL HISTORY
INSERT INTO Medical_History (patient_id, allergies, chronic_conditions, previous_surgeries, current_medications, family_medical_history, smoking_status, alcohol_consumption, notes) VALUES
(1, 'Penicillin', 'Hypertension', 'Appendectomy (2010)', 'Amlodipine 5mg daily', 'Father had diabetes', 'Never', 'None', 'Regular follow-ups needed'),
(2, 'None', 'None', 'None', 'None', 'Mother had heart disease', 'Never', 'None', 'Healthy patient'),
(3, 'Sulfa drugs', 'Type 2 Diabetes, Hypertension', 'Gallbladder removal (2015)', 'Metformin 500mg, Lisinopril 10mg', 'Diabetes runs in family', 'Former', 'None', 'Quit smoking 5 years ago'),
(4, 'Aspirin', 'Asthma', 'None', 'Salbutamol inhaler', 'None significant', 'Never', 'None', 'Carries inhaler always'),
(5, 'None', 'None', 'None', 'None', 'None', 'Never', 'None', 'Pediatric patient, healthy'),
(6, 'Latex', 'Hypothyroidism', 'C-section (2018)', 'Levothyroxine 100mcg', 'Thyroid disorders in family', 'Never', 'None', 'Recently discharged'),
(7, 'None', 'High Cholesterol', 'None', 'Atorvastatin 20mg', 'Father had stroke', 'Current', 'Occasional', 'Counseled about lifestyle changes'),
(8, 'None', 'None', 'None', 'None', 'None', 'Never', 'None', 'Pediatric patient'),
(9, 'Peanuts', 'Arthritis', 'Knee surgery (2020)', 'Ibuprofen as needed', 'Arthritis in family', 'Never', 'None', 'Has EpiPen for allergy'),
(10, 'None', 'None', 'None', 'Prenatal vitamins', 'None', 'Never', 'None', 'Currently pregnant'),
(11, 'None', 'Chronic Kidney Disease', 'None', 'Multiple medications for CKD', 'Kidney disease in family', 'Former', 'None', 'On dialysis'),
(12, 'None', 'None', 'None', 'None', 'None', 'Never', 'None', 'Young healthy patient');

-- 8. ADMISSIONS
INSERT INTO Admissions (patient_id, room_id, admitting_doctor_id, admission_date, expected_discharge_date, actual_discharge_date, diagnosis, admission_type, initial_deposit, status, discharge_summary) VALUES
(1, 4, 1, '2025-11-01 10:30:00', '2025-11-05', NULL, 'Acute Myocardial Infarction', 'Emergency', 50000.00, 'Admitted', NULL),
(2, 6, 3, '2025-10-28 14:00:00', '2025-11-06', NULL, 'Severe Migraine with complications', 'Planned', 30000.00, 'Admitted', NULL),
(3, 8, 2, '2025-10-30 09:00:00', '2025-11-08', NULL, 'Diabetic Ketoacidosis', 'Emergency', 40000.00, 'Admitted', NULL),
(7, 10, 1, '2025-11-02 16:45:00', '2025-11-10', NULL, 'Heart Failure', 'Emergency', 60000.00, 'Admitted', NULL),
(6, 6, 4, '2025-10-20 11:00:00', '2025-10-25', '2025-10-25 15:00:00', 'Pneumonia', 'Emergency', 20000.00, 'Discharged', 'Patient recovered well, completed antibiotic course');

-- 9. APPOINTMENTS
INSERT INTO Appointments (patient_id, doctor_id, appointment_date, appointment_time, appointment_type, status, reason, diagnosis, prescription, notes) VALUES
(5, 2, '2025-11-04', '14:00:00', 'Check-up', 'Scheduled', 'Routine pediatric checkup', NULL, NULL, 'First visit'),
(8, 2, '2025-11-05', '15:00:00', 'Follow-up', 'Scheduled', 'Follow-up after fever', NULL, NULL, NULL),
(9, 5, '2025-11-06', '11:00:00', 'Consultation', 'Scheduled', 'Joint pain evaluation', NULL, NULL, NULL),
(10, 4, '2025-11-04', '10:00:00', 'Consultation', 'Completed', 'Pregnancy checkup', 'Normal pregnancy progress', 'Prenatal vitamins', '20 weeks pregnant'),
(12, 6, '2025-11-04', '13:00:00', 'Consultation', 'Completed', 'Skin rash', 'Allergic dermatitis', 'Antihistamine cream', 'Avoid allergen'),
(1, 1, '2025-10-15', '11:00:00', 'Consultation', 'Completed', 'Chest pain', 'Angina', 'Nitroglycerin prescribed', 'Advised admission'),
(11, 4, '2025-11-07', '09:00:00', 'Follow-up', 'Scheduled', 'Dialysis follow-up', NULL, NULL, NULL),
(4, 4, '2025-11-05', '11:00:00', 'Follow-up', 'Scheduled', 'Asthma management', NULL, NULL, NULL);

-- 10. BILLS
INSERT INTO Bills (patient_id, admission_id, bill_date, total_amount, discount_amount, tax_amount, final_amount, paid_amount, pending_amount, payment_status, bill_type, notes) VALUES
(1, 1, '2025-11-01 11:00:00', 12000.00, 0.00, 0.00, 12000.00, 50000.00, 0.00, 'Partially Paid', 'Admission', 'Advance payment received'),
(2, 2, '2025-10-28 14:30:00', 18000.00, 0.00, 0.00, 18000.00, 30000.00, 0.00, 'Partially Paid', 'Admission', 'Deposit received'),
(3, 3, '2025-10-30 09:30:00', 18000.00, 0.00, 0.00, 18000.00, 40000.00, 0.00, 'Partially Paid', 'Admission', 'Advance payment'),
(7, 4, '2025-11-02 17:00:00', 35000.00, 0.00, 0.00, 35000.00, 60000.00, 0.00, 'Partially Paid', 'Admission', 'ICU admission'),
(6, 5, '2025-10-25 15:30:00', 32500.00, 2500.00, 0.00, 30000.00, 30000.00, 0.00, 'Fully Paid', 'Admission', 'Discount applied, full payment'),
(10, NULL, '2025-11-04 10:30:00', 1500.00, 0.00, 0.00, 1500.00, 1500.00, 0.00, 'Fully Paid', 'Consultation', 'Consultation fee'),
(12, NULL, '2025-11-04 13:30:00', 2000.00, 0.00, 0.00, 2000.00, 2000.00, 0.00, 'Fully Paid', 'Consultation', 'Dermatology consultation');

-- 11. BILL ITEMS
INSERT INTO Bill_Items (bill_id, service_type, description, quantity, unit_price, total_price, service_date) VALUES
(1, 'Room Charge', 'General Ward - 3 days', 3, 800.00, 2400.00, '2025-11-01'),
(1, 'Lab Test', 'Cardiac Enzyme Test', 1, 3500.00, 3500.00, '2025-11-01'),
(1, 'Lab Test', 'ECG', 1, 1500.00, 1500.00, '2025-11-01'),
(1, 'Medicine', 'Cardiac medications', 1, 4600.00, 4600.00, '2025-11-01'),
(2, 'Room Charge', 'Private Room - 7 days', 7, 2000.00, 14000.00, '2025-10-28'),
(2, 'Lab Test', 'MRI Brain', 1, 15000.00, 15000.00, '2025-10-28'),
(2, 'Consultation', 'Neurology consultation', 2, 3000.00, 6000.00, '2025-10-28'),
(3, 'Room Charge', 'Private Room - 8 days', 8, 2000.00, 16000.00, '2025-10-30'),
(3, 'Lab Test', 'Blood Sugar Panel', 1, 2500.00, 2500.00, '2025-10-30'),
(3, 'Medicine', 'Insulin and medications', 1, 8500.00, 8500.00, '2025-10-30'),
(4, 'Room Charge', 'ICU - 7 days', 7, 5000.00, 35000.00, '2025-11-02'),
(5, 'Room Charge', 'Private Room - 5 days', 5, 2000.00, 10000.00, '2025-10-20'),
(5, 'Medicine', 'Antibiotics', 1, 5000.00, 5000.00, '2025-10-20'),
(5, 'Lab Test', 'Chest X-Ray', 2, 2000.00, 4000.00, '2025-10-20'),
(5, 'Consultation', 'Daily rounds', 5, 1500.00, 7500.00, '2025-10-20'),
(6, 'Consultation', 'General physician consultation', 1, 1500.00, 1500.00, '2025-11-04'),
(7, 'Consultation', 'Dermatologist consultation', 1, 2000.00, 2000.00, '2025-11-04');

-- 12. PAYMENTS
INSERT INTO Payments (bill_id, payment_date, amount, payment_method, transaction_reference, received_by, notes) VALUES
(1, '2025-11-01 11:00:00', 50000.00, 'Cash', NULL, 14, 'Initial deposit for admission'),
(2, '2025-10-28 14:30:00', 30000.00, 'Bank Transfer', 'TXN-2025102801', 14, 'Admission deposit'),
(3, '2025-10-30 09:30:00', 40000.00, 'Card', 'CARD-2025103001', 15, 'Emergency admission payment'),
(4, '2025-11-02 17:00:00', 60000.00, 'Cash', NULL, 14, 'ICU admission advance'),
(5, '2025-10-21 10:00:00', 20000.00, 'Cash', NULL, 14, 'Initial deposit'),
(5, '2025-10-25 15:30:00', 10000.00, 'Card', 'CARD-2025102502', 15, 'Final payment at discharge'),
(6, '2025-11-04 10:30:00', 1500.00, 'Cash', NULL, 14, 'Consultation payment'),
(7, '2025-11-04 13:30:00', 2000.00, 'Card', 'CARD-2025110401', 14, 'Dermatology consultation');

-- 13. PRESCRIPTIONS
INSERT INTO Prescriptions (patient_id, doctor_id, appointment_id, admission_id, prescription_date, diagnosis, notes, follow_up_date) VALUES
(10, 4, 4, NULL, '2025-11-04', 'Normal pregnancy - 20 weeks', 'Continue prenatal vitamins, next ultrasound in 4 weeks', '2025-12-02'),
(12, 6, 5, NULL, '2025-11-04', 'Allergic Contact Dermatitis', 'Avoid trigger, apply cream twice daily', '2025-11-18'),
(1, 1, 6, 1, '2025-10-15', 'Unstable Angina', 'Immediate admission required', NULL),
(6, 4, NULL, 5, '2025-10-20', 'Community Acquired Pneumonia', 'Complete full course of antibiotics', '2025-11-05'),
(4, 4, NULL, NULL, '2025-10-28', 'Asthma - Mild Persistent', 'Use inhaler as prescribed', '2025-11-28');

-- 14. PRESCRIPTION ITEMS
INSERT INTO Prescription_Items (prescription_id, medicine_name, dosage, frequency, duration, instructions) VALUES
(1, 'Prenatal Multivitamin', '1 tablet', 'Once daily', 'Continue throughout pregnancy', 'Take with food'),
(1, 'Folic Acid', '5mg', 'Once daily', 'Continue throughout pregnancy', 'Take in morning'),
(2, 'Hydrocortisone Cream 1%', 'Apply thin layer', 'Twice daily', '14 days', 'Apply to affected areas only'),
(2, 'Cetirizine', '10mg', 'Once daily at night', '14 days', 'May cause drowsiness'),
(3, 'Nitroglycerin', '0.4mg sublingual', 'As needed', '30 days', 'For chest pain - sit down before use'),
(3, 'Aspirin', '75mg', 'Once daily', 'Ongoing', 'Take with food'),
(3, 'Atorvastatin', '40mg', 'Once daily at night', 'Ongoing', 'Take at bedtime'),
(4, 'Amoxicillin', '500mg', 'Three times daily', '7 days', 'Complete full course'),
(4, 'Azithromycin', '500mg', 'Once daily', '5 days', 'Take on empty stomach'),
(5, 'Salbutamol Inhaler', '2 puffs', 'Four times daily', '30 days', 'Shake well before use'),
(5, 'Fluticasone Inhaler', '2 puffs', 'Twice daily', '30 days', 'Rinse mouth after use');

-- 15. LAB TESTS
INSERT INTO Lab_Tests (patient_id, doctor_id, test_name, test_type, test_date, report_date, result, status, cost, notes) VALUES
(1, 1, 'Troponin I', 'Blood Test', '2025-11-01', '2025-11-01', 'Elevated - 2.5 ng/mL (Normal <0.04)', 'Completed', 3500.00, 'Indicates myocardial injury'),
(1, 1, 'ECG', 'Cardiac', '2025-11-01', '2025-11-01', 'ST elevation in leads II, III, aVF', 'Completed', 1500.00, 'Consistent with inferior MI'),
(2, 3, 'MRI Brain', 'Imaging', '2025-10-28', '2025-10-29', 'No structural abnormalities detected', 'Completed', 15000.00, 'Migraine without complications'),
(3, 2, 'HbA1c', 'Blood Test', '2025-10-30', '2025-10-30', '11.2% (Poorly controlled)', 'Completed', 1500.00, 'Needs better diabetes management'),
(3, 2, 'Blood Glucose', 'Blood Test', '2025-10-30', '2025-10-30', '425 mg/dL', 'Completed', 500.00, 'Severely elevated'),
(6, 4, 'Chest X-Ray', 'Imaging', '2025-10-20', '2025-10-20', 'Right lower lobe consolidation', 'Completed', 2000.00, 'Consistent with pneumonia'),
(6, 4, 'CBC', 'Blood Test', '2025-10-20', '2025-10-20', 'WBC 15,000 - elevated', 'Completed', 1500.00, 'Indicates infection'),
(10, 4, 'Ultrasound Obstetric', 'Imaging', '2025-11-04', '2025-11-04', 'Normal fetal development at 20 weeks', 'Completed', 3500.00, 'All parameters normal'),
(5, 2, 'Complete Blood Count', 'Blood Test', '2025-11-04', NULL, NULL, 'Pending', 1200.00, 'Routine checkup'),
(11, 4, 'Kidney Function Test', 'Blood Test', '2025-11-03', '2025-11-03', 'Creatinine 4.5 mg/dL, eGFR 18', 'Completed', 2000.00, 'Stage 4 CKD'),
(7, 1, 'BNP Level', 'Blood Test', '2025-11-02', '2025-11-02', '850 pg/mL (Elevated)', 'Completed', 3000.00, 'Indicates heart failure'),
(12, 6, 'Allergy Patch Test', 'Dermatology', '2025-11-04', NULL, NULL, 'In Progress', 4500.00, 'Testing for contact allergens');

-- 16. AMBULANCES
INSERT INTO Ambulances (vehicle_number, ambulance_type, model, year, driver_name, driver_phone, driver_license, status, last_maintenance_date, next_maintenance_date) VALUES
('AMB-001', 'Basic Life Support', 'Toyota Hiace', 2022, 'Rashid Ali', '0300-9876543', 'DL-12345-2020', 'Available', '2025-10-15', '2026-01-15'),
('AMB-002', 'Advanced Life Support', 'Mercedes Sprinter', 2023, 'Iftikhar Ahmed', '0300-8765432', 'DL-23456-2019', 'Available', '2025-10-20', '2026-01-20'),
('AMB-003', 'Basic Life Support', 'Toyota Hiace', 2021, 'Naveed Khan', '0300-7654321', 'DL-34567-2018', 'On Call', '2025-09-30', '2025-12-30'),
('AMB-004', 'Patient Transport', 'Suzuki APV', 2023, 'Asif Mehmood', '0300-6543210', 'DL-45678-2021', 'Available', '2025-10-25', '2026-01-25'),
('AMB-005', 'Advanced Life Support', 'Ford Transit', 2022, 'Zahid Hussain', '0300-5432109', 'DL-56789-2020', 'Under Maintenance', '2025-11-01', '2026-02-01');

-- 17. AMBULANCE BOOKINGS
INSERT INTO Ambulance_Bookings (ambulance_id, patient_id, patient_name, patient_phone, pickup_location, destination, booking_date, booking_time, actual_dispatch_time, actual_arrival_time, distance_km, charges, status, emergency_level, notes) VALUES
(1, 1, 'Ali Hassan', '0321-1111111', 'House 123, Street 5, F-10, Islamabad', 'City Hospital Emergency', '2025-11-01', '10:00:00', '2025-11-01 10:05:00', '2025-11-01 10:25:00', 8.5, 2500.00, 'Completed', 'Critical', 'Cardiac emergency - patient had chest pain'),
(2, 7, 'Kamran Hussain', '0321-7777777', 'Plot 45, Sector E, Islamabad', 'City Hospital ICU', '2025-11-02', '16:30:00', '2025-11-02 16:35:00', '2025-11-02 16:50:00', 6.2, 3500.00, 'Completed', 'Critical', 'Heart failure patient'),
(3, NULL, 'Sadia Akhtar', '0321-1122334', 'Bahria Town Phase 4, Rawalpindi', 'City Hospital Emergency', '2025-11-04', '14:30:00', '2025-11-04 14:35:00', NULL, 12.0, 3000.00, 'Dispatched', 'High', 'Road accident victim'),
(4, 6, 'Zainab Ali', '0321-6666666', 'City Hospital', 'House 234, G-11, Islamabad', '2025-10-25', '15:30:00', '2025-10-25 15:35:00', '2025-10-25 15:55:00', 7.0, 1500.00, 'Completed', 'Low', 'Patient discharge transport'),
(1, NULL, 'Ahmed Raza', '0321-9988776', 'I-8 Markaz, Islamabad', 'City Hospital', '2025-11-05', '10:00:00', NULL, NULL, 5.0, 2000.00, 'Confirmed', 'Medium', 'Scheduled transfer'),
(2, 11, 'Tariq Aziz', '0333-1111111', 'House 45, F-7, Islamabad', 'Dialysis Center', '2025-11-04', '08:00:00', '2025-11-04 08:05:00', '2025-11-04 08:20:00', 4.5, 1800.00, 'Completed', 'Low', 'Regular dialysis transport');

-- 18. INVENTORY
INSERT INTO Inventory (item_name, item_type, category, quantity_available, unit_price, reorder_level, expiry_date, supplier, status) VALUES
('Paracetamol 500mg', 'Medicine', 'Pain Relief', 5000, 2.00, 1000, '2026-12-31', 'PharmaCorp', 'Available'),
('Amoxicillin 500mg', 'Medicine', 'Antibiotic', 3000, 5.00, 500, '2026-08-31', 'MediSupply', 'Available'),
('Insulin (Vial)', 'Medicine', 'Diabetes', 200, 850.00, 50, '2026-06-30', 'DiabetesCare Inc', 'Available'),
('Surgical Gloves (Box)', 'Consumable', 'PPE', 500, 350.00, 100, NULL, 'MedEquip Ltd', 'Available'),
('Syringes 5ml (Pack of 100)', 'Consumable', 'Medical Supplies', 800, 450.00, 200, NULL, 'MediSupply', 'Available'),
('Gauze Bandages', 'Consumable', 'Wound Care', 1200, 50.00, 300, NULL, 'SurgicalSupplies Co', 'Available'),
('BP Monitor', 'Equipment', 'Diagnostic', 25, 8500.00, 5, NULL, 'MedTech Solutions', 'Available'),
('Wheelchair', 'Equipment', 'Patient Care', 15, 12000.00, 3, NULL, 'HealthEquip', 'Available'),
('Oxygen Cylinder', 'Equipment', 'Respiratory', 40, 15000.00, 10, NULL, 'OxygenSupply Inc', 'Available'),
('Lisinopril 10mg', 'Medicine', 'Cardiovascular', 800, 8.00, 200, '2026-10-31', 'PharmaCorp', 'Available'),
('Metformin 500mg', 'Medicine', 'Diabetes', 2500, 3.50, 500, '2026-09-30', 'MediSupply', 'Available'),
('Aspirin 75mg', 'Medicine', 'Cardiovascular', 1500, 1.50, 300, '2027-01-31', 'PharmaCorp', 'Available'),
('N95 Masks (Box)', 'Consumable', 'PPE', 300, 1200.00, 100, NULL, 'PPE Supplies', 'Available'),
('IV Fluid (Saline)', 'Consumable', 'IV Solutions', 600, 120.00, 150, '2026-07-31', 'MediSupply', 'Available'),
('ECG Machine', 'Equipment', 'Diagnostic', 8, 250000.00, 2, NULL, 'CardioTech', 'Available'),
('Azithromycin 500mg', 'Medicine', 'Antibiotic', 450, 12.00, 100, '2026-05-31', 'PharmaCorp', 'Available'),
('Surgical Masks (Box)', 'Consumable', 'PPE', 95, 800.00, 100, NULL, 'PPE Supplies', 'Low Stock'),
('Thermometer (Digital)', 'Equipment', 'Diagnostic', 45, 1500.00, 15, NULL, 'MedTech Solutions', 'Available'),
('Atorvastatin 20mg', 'Medicine', 'Cardiovascular', 1800, 6.00, 400, '2026-11-30', 'MediSupply', 'Available'),
('Salbutamol Inhaler', 'Medicine', 'Respiratory', 250, 450.00, 80, '2026-04-30', 'RespiratoryCare', 'Available');

-- 19. AUDIT LOG
INSERT INTO Audit_Log (user_id, action_type, table_name, record_id, old_value, new_value, ip_address, description, timestamp) VALUES
(1, 'LOGIN', NULL, NULL, NULL, NULL, '192.168.1.100', 'Admin logged in', '2025-11-04 08:00:00'),
(2, 'LOGIN', NULL, NULL, NULL, NULL, '192.168.1.101', 'Dr. Ahmed Khan logged in', '2025-11-04 09:15:00'),
(9, 'INSERT', 'Patients', 1, NULL, 'New patient: Ali Hassan', '192.168.1.102', 'New patient registered', '2025-10-15 09:30:00'),
(2, 'INSERT', 'Appointments', 6, NULL, 'Appointment for Ali Hassan', '192.168.1.101', 'Appointment scheduled', '2025-10-15 11:00:00'),
(2, 'UPDATE', 'Appointments', 6, 'Status: Scheduled', 'Status: Completed', '192.168.1.101', 'Appointment completed', '2025-10-15 12:00:00'),
(14, 'INSERT', 'Admissions', 1, NULL, 'Patient Ali Hassan admitted', '192.168.1.103', 'Emergency admission', '2025-11-01 10:30:00'),
(14, 'INSERT', 'Bills', 1, NULL, 'Bill created for admission', '192.168.1.103', 'Billing for admission', '2025-11-01 11:00:00'),
(14, 'INSERT', 'Payments', 1, NULL, 'Payment of 50000 received', '192.168.1.103', 'Payment recorded', '2025-11-01 11:05:00'),
(3, 'INSERT', 'Prescriptions', 1, NULL, 'Prescription for Ayesha Khan', '192.168.1.104', 'Prescription created', '2025-11-04 10:30:00'),
(10, 'INSERT', 'Patients', 12, NULL, 'New patient: Hira Farooq', '192.168.1.102', 'New patient registered', '2025-11-04 12:00:00'),
(1, 'UPDATE', 'Rooms', 15, 'Status: Available', 'Status: Under Maintenance', '192.168.1.100', 'Room status updated', '2025-11-03 16:00:00'),
(14, 'UPDATE', 'Admissions', 5, 'Status: Admitted', 'Status: Discharged', '192.168.1.103', 'Patient discharged', '2025-10-25 15:00:00'),
(15, 'UPDATE', 'Bills', 5, 'Status: Partially Paid', 'Status: Fully Paid', '192.168.1.105', 'Bill fully paid', '2025-10-25 15:30:00'),
(9, 'INSERT', 'Ambulance_Bookings', 1, NULL, 'Ambulance booked for Ali Hassan', '192.168.1.102', 'Emergency ambulance', '2025-11-01 10:00:00'),
(1, 'UPDATE', 'Users', 5, 'Status: Active', 'Status: Active', '192.168.1.100', 'User details updated', '2025-11-03 10:00:00');

-- ============================================
-- UPDATE BILL TOTALS (Recalculate from items)
-- ============================================

UPDATE Bills b
SET total_amount = (
    SELECT IFNULL(SUM(total_price), 0)
    FROM Bill_Items bi
    WHERE bi.bill_id = b.bill_id
),
final_amount = (
    SELECT IFNULL(SUM(total_price), 0)
    FROM Bill_Items bi
    WHERE bi.bill_id = b.bill_id
) - b.discount_amount + b.tax_amount,
pending_amount = (
    SELECT IFNULL(SUM(total_price), 0)
    FROM Bill_Items bi
    WHERE bi.bill_id = b.bill_id
) - b.discount_amount + b.tax_amount - b.paid_amount;

-- Update payment status based on calculations
UPDATE Bills 
SET payment_status = CASE 
    WHEN pending_amount <= 0 THEN 'Fully Paid'
    WHEN paid_amount > 0 AND pending_amount > 0 THEN 'Partially Paid'
    WHEN DATEDIFF(CURDATE(), bill_date) > 30 AND pending_amount > 0 THEN 'Overdue'
    ELSE 'Pending'
END;

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

SELECT 'Database populated successfully!' AS Status;

SELECT 'Users' AS Table_Name, COUNT(*) AS Record_Count FROM Users
UNION ALL
SELECT 'Departments', COUNT(*) FROM Departments
UNION ALL
SELECT 'Staff', COUNT(*) FROM Staff
UNION ALL
SELECT 'Doctors', COUNT(*) FROM Doctors
UNION ALL
SELECT 'Patients', COUNT(*) FROM Patients
UNION ALL
SELECT 'Rooms', COUNT(*) FROM Rooms
UNION ALL
SELECT 'Admissions', COUNT(*) FROM Admissions
UNION ALL
SELECT 'Appointments', COUNT(*) FROM Appointments
UNION ALL
SELECT 'Bills', COUNT(*) FROM Bills
UNION ALL
SELECT 'Bill_Items', COUNT(*) FROM Bill_Items
UNION ALL
SELECT 'Payments', COUNT(*) FROM Payments
UNION ALL
SELECT 'Prescriptions', COUNT(*) FROM Prescriptions
UNION ALL
SELECT 'Prescription_Items', COUNT(*) FROM Prescription_Items
UNION ALL
SELECT 'Lab_Tests', COUNT(*) FROM Lab_Tests
UNION ALL
SELECT 'Ambulances', COUNT(*) FROM Ambulances
UNION ALL
SELECT 'Ambulance_Bookings', COUNT(*) FROM Ambulance_Bookings
UNION ALL
SELECT 'Inventory', COUNT(*) FROM Inventory
UNION ALL
SELECT 'Medical_History', COUNT(*) FROM Medical_History
UNION ALL
SELECT 'Audit_Log', COUNT(*) FROM Audit_Log;

-- ============================================
-- END OF SAMPLE DATA INSERT
-- ============================================
SET SQL_SAFE_UPDATES = 1;

DELIMITER $$

CREATE  PROCEDURE sp_record_payment(
    IN p_bill_id INT,
    IN p_amount DECIMAL(10,2),
    IN p_payment_method VARCHAR(20),
    IN p_transaction_ref VARCHAR(100),
    IN p_received_by INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Insert payment record
    INSERT INTO Payments (bill_id, amount, payment_method, transaction_reference, received_by)
    VALUES (p_bill_id, p_amount, p_payment_method, p_transaction_ref, p_received_by);

    -- Update bill amounts correctly
    UPDATE Bills 
    SET paid_amount = paid_amount + p_amount,
        pending_amount = GREATEST(final_amount - (paid_amount + p_amount), 0),
        payment_status = CASE 
            WHEN (paid_amount + p_amount) >= final_amount THEN 'Fully Paid'
            WHEN (paid_amount + p_amount) > 0 THEN 'Partially Paid'
            ELSE 'Pending'
        END
    WHERE bill_id = p_bill_id;

    COMMIT;
END $$

DELIMITER ;