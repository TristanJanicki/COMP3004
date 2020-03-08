import enum

class StatusEnum(enum.Enum):
    updated_requested = "update_requested"
    up_to_date = "up_to_date"
    current_updating = "current_updating"
