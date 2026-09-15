const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 8080;
const JWT_SECRET = '404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971';

app.use(cors());
app.use(express.json());

// --- IN-MEMORY SEEDED DATABASE ---

let users = [
  {
    id: 1,
    name: 'Rahul Sharma',
    email: 'patient@medibook.com',
    password: 'password123',
    phone: '+91 98765 43210',
    role: 'PATIENT'
  },
  {
    id: 2,
    name: 'Arun Kumar',
    email: 'doctor@medibook.com',
    password: 'password123',
    phone: '+91 91234 56789',
    role: 'DOCTOR'
  },
  {
    id: 3,
    name: 'Priya Sharma',
    email: 'priya@medibook.com',
    password: 'password123',
    phone: '+91 98111 22233',
    role: 'DOCTOR'
  },
  {
    id: 4,
    name: 'Rahul Menon',
    email: 'rahul@medibook.com',
    password: 'password123',
    phone: '+91 97444 55566',
    role: 'DOCTOR'
  },
  {
    id: 5,
    name: 'Ananya Rao',
    email: 'ananya@medibook.com',
    password: 'password123',
    phone: '+91 96333 44455',
    role: 'DOCTOR'
  }
];

let departments = [
  { id: 1, name: 'Cardiology', icon: 'Heart', description: 'Heart and cardiovascular health care by expert cardiologists.', doctorCount: 15 },
  { id: 2, name: 'Neurology', icon: 'Brain', description: 'Specialized treatment for brain, nerve, and spine disorders.', doctorCount: 12 },
  { id: 3, name: 'Dermatology', icon: 'Sparkles', description: 'Comprehensive skin, hair, and skincare treatments.', doctorCount: 18 },
  { id: 4, name: 'General Medicine', icon: 'Stethoscope', description: 'Primary care, routine checkups, and general diagnosis.', doctorCount: 25 },
  { id: 5, name: 'Dentistry', icon: 'Smile', description: 'Dental hygiene, cosmetic surgery, and orthodontic care.', doctorCount: 10 },
  { id: 6, name: 'Ophthalmology', icon: 'Eye', description: 'Advanced eye checkups, vision therapy, and laser treatments.', doctorCount: 8 }
];

let doctors = [
  {
    id: 1,
    userId: 2,
    name: 'Dr. Arun Kumar',
    specialization: 'Cardiology',
    experience: 10,
    hospital: 'ABC Medical Center',
    consultationFee: 500.0,
    rating: 4.8,
    bio: 'Dr. Arun Kumar is an experienced senior cardiologist specializing in interventional cardiology, heart preventive care, and hypertension management.',
    avatarUrl: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=400&q=80',
    availableDays: 'Monday, Wednesday, Friday',
    isAvailableToday: true
  },
  {
    id: 2,
    userId: 3,
    name: 'Dr. Priya Sharma',
    specialization: 'Dermatology',
    experience: 8,
    hospital: 'Apollo Skin Clinic',
    consultationFee: 400.0,
    rating: 4.9,
    bio: 'Dr. Priya Sharma is a certified dermatologist with 8+ years of expertise in clinical skincare, acne therapy, and aesthetic procedures.',
    avatarUrl: 'https://images.unsplash.com/photo-1594824813566-88855ce783d1?auto=format&fit=crop&w=400&q=80',
    availableDays: 'Tuesday, Thursday, Saturday',
    isAvailableToday: true
  },
  {
    id: 3,
    userId: 4,
    name: 'Dr. Rahul Menon',
    specialization: 'Neurology',
    experience: 12,
    hospital: 'Fortis Healthcare Center',
    consultationFee: 700.0,
    rating: 4.7,
    bio: 'Dr. Rahul Menon is a leading neurospecialist focusing on migraine therapies, stroke management, and nerve disorder rehabilitations.',
    avatarUrl: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=400&q=80',
    availableDays: 'Monday, Tuesday, Thursday',
    isAvailableToday: false
  },
  {
    id: 4,
    userId: 5,
    name: 'Dr. Ananya Rao',
    specialization: 'General Medicine',
    experience: 7,
    hospital: 'City Care Hospital',
    consultationFee: 300.0,
    rating: 4.6,
    bio: 'Dr. Ananya Rao provides holistic primary healthcare, routine preventative checkups, and diagnostic consultations for families.',
    avatarUrl: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=400&q=80',
    availableDays: 'Monday, Wednesday, Saturday',
    isAvailableToday: true
  }
];

// Generate Seed Availability Slots for next 7 days
let availability = [];
let availIdCounter = 1;
const today = new Date();
const timeList = ['09:00:00', '09:30:00', '10:00:00', '10:30:00', '11:00:00', '14:00:00', '14:30:00', '15:00:00'];

doctors.forEach(doc => {
  for (let i = 0; i < 7; i++) {
    const d = new Date(today);
    d.setDate(d.getDate() + i);
    const dateStr = d.toISOString().split('T')[0];

    timeList.forEach(t => {
      availability.push({
        id: availIdCounter++,
        doctor: doc,
        availableDate: dateStr,
        startTime: t,
        endTime: t.replace(/:\d\d$/, ':30'),
        isBooked: false
      });
    });
  }
});

// Seed Initial Demo Appointments
const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
const dateTomorrow = tomorrow.toISOString().split('T')[0];

let appointments = [
  {
    id: 1,
    patient: users[0],
    doctor: doctors[0],
    appointmentDate: dateTomorrow,
    appointmentTime: '10:00:00',
    status: 'CONFIRMED',
    notes: 'Routine cardiac wellness checkup.'
  },
  {
    id: 2,
    patient: users[0],
    doctor: doctors[1],
    appointmentDate: dateTomorrow,
    appointmentTime: '14:30:00',
    status: 'PENDING',
    notes: 'Consultation regarding skin allergy.'
  }
];

// Mark booked slot
const matchingSlot = availability.find(a => a.doctor.id === 1 && a.availableDate === dateTomorrow && a.startTime === '10:00:00');
if (matchingSlot) matchingSlot.isBooked = true;

// --- AUTH MIDDLEWARE ---
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ message: 'Authorization token required' });

  jwt.verify(token, JWT_SECRET, (err, decoded) => {
    if (err) return res.status(403).json({ message: 'Invalid or expired token' });
    req.user = decoded;
    next();
  });
};

// --- AUTH ENDPOINTS ---

app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;
  const user = users.find(u => u.email === email && u.password === password);

  if (!user) {
    return res.status(400).json({ message: 'Invalid email or password' });
  }

  const doctor = doctors.find(d => d.userId === user.id);
  const token = jwt.sign({ id: user.id, email: user.email, role: user.role }, JWT_SECRET, { expiresIn: '24h' });

  res.json({
    token,
    type: 'Bearer',
    id: user.id,
    name: user.name,
    email: user.email,
    role: user.role,
    doctorId: doctor ? doctor.id : null
  });
});

app.post('/api/auth/register', (req, res) => {
  const { name, email, password, phone, role, specialization, experience, hospital, consultationFee, bio } = req.body;

  if (users.some(u => u.email === email)) {
    return res.status(400).json({ message: 'Email address is already in use!' });
  }

  const newUser = {
    id: users.length + 1,
    name,
    email,
    password,
    phone: phone || '',
    role: role || 'PATIENT'
  };
  users.push(newUser);

  let newDoctor = null;
  if (role === 'DOCTOR') {
    newDoctor = {
      id: doctors.length + 1,
      userId: newUser.id,
      name: `Dr. ${name}`,
      specialization: specialization || 'General Medicine',
      experience: parseInt(experience) || 5,
      hospital: hospital || 'MediBook Health Center',
      consultationFee: parseFloat(consultationFee) || 500,
      rating: 4.8,
      bio: bio || 'Experienced practitioner committed to quality care.',
      avatarUrl: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=400&q=80',
      availableDays: 'Monday, Wednesday, Friday',
      isAvailableToday: true
    };
    doctors.push(newDoctor);
  }

  const token = jwt.sign({ id: newUser.id, email: newUser.email, role: newUser.role }, JWT_SECRET, { expiresIn: '24h' });

  res.json({
    token,
    type: 'Bearer',
    id: newUser.id,
    name: newUser.name,
    email: newUser.email,
    role: newUser.role,
    doctorId: newDoctor ? newDoctor.id : null
  });
});

app.get('/api/auth/me', authenticateToken, (req, res) => {
  const user = users.find(u => u.id === req.user.id);
  if (!user) return res.status(444).json({ message: 'User not found' });
  res.json(user);
});

// --- DOCTOR ENDPOINTS ---

app.get('/api/doctors', (req, res) => {
  const { search, dept, sort } = req.query;
  let result = [...doctors];

  if (search) {
    const q = search.toLowerCase();
    result = result.filter(d =>
      d.name.toLowerCase().includes(q) ||
      d.specialization.toLowerCase().includes(q) ||
      d.hospital.toLowerCase().includes(q)
    );
  }

  if (dept && dept !== 'All') {
    result = result.filter(d => d.specialization.toLowerCase() === dept.toLowerCase());
  }

  if (sort === 'rating') {
    result.sort((a, b) => b.rating - a.rating);
  } else if (sort === 'experience') {
    result.sort((a, b) => b.experience - a.experience);
  } else if (sort === 'fee_low') {
    result.sort((a, b) => a.consultationFee - b.consultationFee);
  }

  res.json(result);
});

app.get('/api/doctors/:id', (req, res) => {
  const doctor = doctors.find(d => d.id === parseInt(req.params.id));
  if (!doctor) return res.status(404).json({ message: 'Doctor not found' });
  res.json(doctor);
});

app.put('/api/doctors/:id', authenticateToken, (req, res) => {
  const doctor = doctors.find(d => d.id === parseInt(req.params.id));
  if (!doctor) return res.status(404).json({ message: 'Doctor not found' });

  Object.assign(doctor, req.body);
  res.json(doctor);
});

// --- DEPARTMENT ENDPOINTS ---

app.get('/api/departments', (req, res) => {
  res.json(departments);
});

// --- APPOINTMENT ENDPOINTS ---

app.post('/api/appointments', authenticateToken, (req, res) => {
  const { doctorId, appointmentDate, appointmentTime, notes } = req.body;
  const user = users.find(u => u.id === req.user.id);
  const doctor = doctors.find(d => d.id === parseInt(doctorId));

  if (!doctor) return res.status(404).json({ message: 'Doctor not found' });

  // Double-booking check
  const isBooked = appointments.some(a =>
    a.doctor.id === doctor.id &&
    a.appointmentDate === appointmentDate &&
    a.appointmentTime === appointmentTime &&
    a.status !== 'CANCELLED'
  );

  if (isBooked) {
    return res.status(400).json({ message: `Selected time slot is already booked for ${doctor.name}. Please choose another time slot.` });
  }

  const newAppointment = {
    id: appointments.length + 1,
    patient: user,
    doctor,
    appointmentDate,
    appointmentTime,
    status: 'CONFIRMED',
    notes: notes || ''
  };

  appointments.push(newAppointment);

  // Update availability slot state
  const availSlot = availability.find(a => a.doctor.id === doctor.id && a.availableDate === appointmentDate && a.startTime.startsWith(appointmentTime.substring(0, 5)));
  if (availSlot) availSlot.isBooked = true;

  res.json(newAppointment);
});

app.get('/api/appointments/my', authenticateToken, (req, res) => {
  const userApps = appointments.filter(a => a.patient.id === req.user.id);
  res.json(userApps.reverse());
});

app.get('/api/appointments/doctor', authenticateToken, (req, res) => {
  const doctor = doctors.find(d => d.userId === req.user.id);
  if (!doctor) return res.status(400).json({ message: 'Doctor profile not found' });

  const docApps = appointments.filter(a => a.doctor.id === doctor.id);
  res.json(docApps.reverse());
});

app.put('/api/appointments/:id/cancel', authenticateToken, (req, res) => {
  const appItem = appointments.find(a => a.id === parseInt(req.params.id));
  if (!appItem) return res.status(404).json({ message: 'Appointment not found' });

  appItem.status = 'CANCELLED';

  const availSlot = availability.find(a => a.doctor.id === appItem.doctor.id && a.availableDate === appItem.appointmentDate && a.startTime.startsWith(appItem.appointmentTime.substring(0, 5)));
  if (availSlot) availSlot.isBooked = false;

  res.json(appItem);
});

app.put('/api/appointments/:id/approve', authenticateToken, (req, res) => {
  const appItem = appointments.find(a => a.id === parseInt(req.params.id));
  if (!appItem) return res.status(404).json({ message: 'Appointment not found' });

  appItem.status = 'CONFIRMED';
  res.json(appItem);
});

app.put('/api/appointments/:id/reject', authenticateToken, (req, res) => {
  const appItem = appointments.find(a => a.id === parseInt(req.params.id));
  if (!appItem) return res.status(404).json({ message: 'Appointment not found' });

  appItem.status = 'CANCELLED';
  res.json(appItem);
});

app.get('/api/appointments/patient-stats', authenticateToken, (req, res) => {
  const userApps = appointments.filter(a => a.patient.id === req.user.id);
  res.json({
    total: userApps.length,
    upcoming: userApps.filter(a => a.status === 'CONFIRMED' || a.status === 'PENDING').length,
    completed: userApps.filter(a => a.status === 'COMPLETED').length,
    cancelled: userApps.filter(a => a.status === 'CANCELLED').length
  });
});

app.get('/api/appointments/doctor-stats', authenticateToken, (req, res) => {
  const doctor = doctors.find(d => d.userId === req.user.id);
  if (!doctor) return res.json({ todayCount: 0, pendingRequests: 0, completedCount: 0, totalPatients: 0 });

  const docApps = appointments.filter(a => a.doctor.id === doctor.id);
  const patientIds = new Set(docApps.map(a => a.patient.id));

  res.json({
    todayCount: docApps.filter(a => a.status === 'CONFIRMED' || a.status === 'PENDING').length,
    pendingRequests: docApps.filter(a => a.status === 'PENDING').length,
    completedCount: docApps.filter(a => a.status === 'COMPLETED').length,
    totalPatients: patientIds.size
  });
});

// --- AVAILABILITY ENDPOINTS ---

app.get('/api/availability/:doctorId', (req, res) => {
  const docId = parseInt(req.params.doctorId);
  const { date } = req.query;

  let slots = availability.filter(a => a.doctor.id === docId);
  if (date) {
    slots = slots.filter(a => a.availableDate === date);
  }

  res.json(slots);
});

app.post('/api/availability', authenticateToken, (req, res) => {
  const doctor = doctors.find(d => d.userId === req.user.id);
  if (!doctor) return res.status(400).json({ message: 'Doctor profile not found' });

  const { availableDate, startTime, endTime } = req.body;
  const newSlot = {
    id: availability.length + 1,
    doctor,
    availableDate,
    startTime,
    endTime,
    isBooked: false
  };

  availability.push(newSlot);
  res.json(newSlot);
});

app.delete('/api/availability/:id', authenticateToken, (req, res) => {
  availability = availability.filter(a => a.id !== parseInt(req.params.id));
  res.sendStatus(204);
});

app.listen(PORT, () => {
  console.log(`🏥 MediBook REST API Backend running on port ${PORT}`);
});
