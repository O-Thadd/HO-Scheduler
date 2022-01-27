# HO-Scheduler
Android app that creates call duty roster for a month for intern doctors(House Officers) in University College Hospital, Ibadan (OBGYN Dept.)

## Background
There are a total of 7 duty posts. Two of them are active calls and the other five, non-active calls.
The active calls require 2 interns while the non-active calls require a single intern.

On weekends, call starts by 8AM and ends 8AM the following day(24Hrs).  
On weekdays except wednesday, call starts by 5PM and ends 8AM the following day(15Hrs).  
On Wednesdays, call starts at Noon and ends 8AM the following day(20Hrs)

Some interns are placed on active call postings(Interns on outside posting). These interns would have to man the active call duty posts until call starts, when the interns on call for that day would then take over.
This outside posting duty lasts for a week(5 days), afterwhich some other interns take over the outside posting.  
**Note: interns on outside posting could still be on active call**

## Conditions
- Equal number of total calls or maximum of 1 call difference.
- Equal number of active, weekend, Wednesday and SW4 calls. or maximum of 1 call difference.  
*weekend calls last the longest(24Hrs), followed by Wednesday calls(20Hrs). SW4 is the busiest of all the non-active call duty posts.*
- Interns on outside posting may have active calls, but only on Tuesdays and Fridays.  
*they're on outside posting, it means they'll keep working after call ends, since outposting duty resumes at end of call. However, active calls on Tuesday and Friday are the best for them, since outside posting lasts only 4Hrs on Wednesday(calls starts at Noon on wednesay) and outside posting ends on Friday(so no outside posting on saturday)*
- For new interns: No active or non-active call within the first 5days or 3days respectively of resumption of duty in OBGYN Dept.
- Atleast one of the pair of interns on active call must have been on active call before.


## Data Required 
- The Month and Year that the roster(schedule) is meant for.
- Name of roster(users choice)
- Intern Data
  - Name
  - Period of outside posting, if on outside posting that month.
  - Period of leave, if on leave that month.
  - Resumption date, if resuming to OBGYN that month.
  - Exit date, if rotating out of OBGYN before that month ends.
  
### App Task
To generate a roster for the given month, fulfilling all the conditions.
